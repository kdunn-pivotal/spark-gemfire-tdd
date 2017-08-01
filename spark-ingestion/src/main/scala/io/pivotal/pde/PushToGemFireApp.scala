package io.pivotal.sample

import java.util.ArrayList
import java.math.BigInteger
import java.util.Properties
import java.util.concurrent.TimeUnit

import BigInt._
import scala.collection.mutable.Queue
import org.apache.geode.cache.Cache
import org.apache.geode.cache.CacheFactory
import org.apache.geode.cache.DataPolicy
import org.apache.geode.cache.Region
import org.apache.geode.cache.client.ClientCache
import org.apache.geode.cache.client.ClientCacheFactory
import org.apache.geode.cache.client.ClientRegionFactory
import org.apache.geode.cache.client.ClientRegionShortcut
import org.apache.geode.cache.RegionShortcut
import org.apache.geode.pdx.ReflectionBasedAutoSerializer

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import org.apache.http.HttpHeaders
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.entity.StringEntity;
import com.google.gson.Gson

import io.pivotal.pde.model.Order
import io.pivotal.pde.model.OrderLineItem

/*
Encapsulating the GemFire objects in a single container object allows them to
be serialized correctly for use on the Spark workers, workaround credit to:
https://www.nicolaferraro.me/2016/02/22/using-non-serializable-objects-in-apache-spark/
 */
object gemfire {

  /*
  The following are used for the dev/test "profile"
   */
  var embRegion: Region[_, _] = null
  var embCache: Cache = null

  def setupGemfireEmbedded(embeddedLocatorHostPort: String, embeddedServerPort: Integer): Cache = {
    var gemfireProperties = new Properties()
    gemfireProperties.setProperty("log-level", System.getProperty("gemfire.log.level", "config"))
    gemfireProperties.setProperty("start-locator", System.getProperty("gemfire.locator.host-port", embeddedLocatorHostPort));
    gemfireProperties.setProperty("start-dev-rest-api", System.getProperty("gemfire.start-dev-rest-api", "true"));
    gemfireProperties.setProperty("http-service-port", System.getProperty("gemfire.http-service-port", "38080"));

    var cf = new CacheFactory(gemfireProperties)
      .set("locators", embeddedLocatorHostPort)

    // Required for the complex types in the model
    cf.setPdxSerializer(new ReflectionBasedAutoSerializer("io.pivotal.pde.model.*"))

    var c = cf.create()

    var cs = c.addCacheServer()
    cs.setPort(embeddedServerPort)
    cs.start()

    embCache = c

    return c
  }

  def setupEmbeddedRegion(cache: Cache, regionName: String): Region[_, _] = {
    return cache.createRegionFactory(RegionShortcut.PARTITION).create(regionName)
  }

  def embeddedRegion(embeddedLocatorHostPort: String, embeddedServerPort: Integer, regionName: String) = {
    var cache: Cache = null

    if (embCache == null) {
      cache = setupGemfireEmbedded(embeddedLocatorHostPort, embeddedServerPort)
    }
    else {
      cache = embCache
    }
    val _embeddedRegion = setupEmbeddedRegion(cache, regionName)

    embRegion = _embeddedRegion

    _embeddedRegion
  }
}

/**
  * Spark Streaming with automated tests
  */
object PushToGemFireApp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("PushToGemFireApp")

    val ssc = new StreamingContext(conf, Seconds(5))

    val rdd = ssc.sparkContext.textFile("file:///Users/kdunn/gdrive/SampleData/retail_demo/orders/orders.tsv.gz")
    val rddQueue: Queue[RDD[String]] = Queue()
    rddQueue += rdd

    val stream = ssc.queueStream(rddQueue)
    //stream.print()

    //processStream(args, stream)
    gfProcessStream(stream, "172.16.139.1", 10334, "Orders", "28080", false)

    ssc.start()
    ssc.awaitTermination()

  }

  def processStream(args: Array[String], stream: DStream[String]): Unit = {
    args match {
      case Array(_, _, path, _*) => stream.saveAsTextFiles(args(2))
      case _ => return
    }
  }

  def makeOrderJsonString(recordElements: String): String = {

    val fields = recordElements.split('\t').map(_.trim)

    val newOrder = new Order(new BigInteger(fields(0)), fields(9).toFloat, fields(10).toFloat)

    val jsonString = new Gson().toJson(newOrder).toString
    println(jsonString)

    return jsonString
  }

  def makeOrderLineItemJsonString(recordElements: String): String = {

    val fields = recordElements.split('\t').map(_.trim)

    val newOrderLineItem = new OrderLineItem(
      new BigInteger(fields(0)), new BigInteger(fields(1)), fields(3), fields(15).toFloat, fields(16).toFloat)

    val jsonString = new Gson().toJson(newOrderLineItem).toString
    println(jsonString)

    return jsonString
  }

  def postToGemFireREST(record: String, locatorHost: String, restPort: String, regionName: String): Unit = {
    val client = new DefaultHttpClient

    //"curl -H 'Content-Type: application/json' -X POST 10.80.2.172:28080/gemfire-api/v1/{region}?key={key} -d'{data}' "

    var thisKey: String = null
    try {
      val rowElements = record.split('\t').map(_.trim)

      if (regionName == "Orders") {
        thisKey = rowElements(0)
      }
      else if (regionName == "OrderLineItems") {
        thisKey = rowElements(1)
      }
      else {
        thisKey = record
      }

    } catch {
      case e: java.util.NoSuchElementException => println(record)
    }

    if (thisKey != null) {

      val gemfireUrl = "http://" + locatorHost + ":" + restPort + "/gemfire-api/v1/" + regionName + "?key=" + thisKey
      //println(gemfireUrl)

      val post = new HttpPost(gemfireUrl)
      post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")

      if (regionName == "Orders" ) {
        post.setEntity(new StringEntity(makeOrderJsonString(record)))
      }
      else if (regionName == "OrderLineItems") {
        post.setEntity(new StringEntity(makeOrderLineItemJsonString(record)))
      }
      else {
        post.setEntity(new StringEntity("{ \"key\" : \"" + thisKey + "\" }"))
      }

      var response: org.apache.http.client.methods.CloseableHttpResponse = null
      try {
        response = client.execute(post)
      } catch {
        case ce: java.net.ConnectException => println("Couldn't connect")
        case nse: java.util.NoSuchElementException => println("No such element")
      }

      if (response != null) {
        //println(response)
      }
    }
  }

  // executed by the worker/executor thread
  def gfProcessStream(stream: DStream[String], locatorHost: String, locatorPort: Integer, regionName: String, restPort: String, isTest: Boolean): Unit = {

    stream.foreachRDD { rdd =>
      rdd.foreachPartition { records =>
          records.foreach( record => postToGemFireREST(record, locatorHost, restPort, regionName) )
      }
    }
  }

  def gfProcessSimple(stream: DStream[String], locatorHost: String, locatorPort: Integer, regionName: String, embeddedServerPort: Integer, isTest: Boolean): Unit = {

    stream.foreachRDD { rdd =>
      rdd.foreachPartition { record =>
        var region: Region[String, String] = null

        if (isTest) {
          region = gemfire.embeddedRegion(locatorHost + "[" + locatorPort + "]", embeddedServerPort, regionName).asInstanceOf[Region[String, String]]
        }

        record.foreach { el =>
          //val rowElements = el.split('\t').map(_.trim)

          //rowElements.foreach(println)

          //var thisOrder = new Order(new BigInteger(rowElements(0)))

          region.put(el, el)
          //println(thisOrder.toString)
        }
      }
    }
  }

}

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

    cf.setPdxPersistent(true)
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
    var cache = setupGemfireEmbedded(embeddedLocatorHostPort, embeddedServerPort)
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
    gfProcessStream(stream, "172.16.139.1", 10334, "Orders", 0, false)

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
    val header = List("order_id",
              "customer_id",
              "store_id",
              "order_datetime",
              "ship_completion_datetime",
              "return_datetime",
              "refund_datetime",
              "payment_method_code",
              "total_tax_amount",
              "total_paid_amount",
              "total_item_quantity",
              "total_discount_amount",
              "coupon_code",
              "coupon_amount",
              "order_canceled_flag",
              "has_returned_items_flag",
              "has_refunded_items_flag",
              "fraud_code",
              "fraud_resolution_code",
              "billing_address_line1",
              "billing_address_line2",
              "billing_address_line3",
              "billing_address_city",
              "billing_address_state",
              "billing_address_postal_code",
              "billing_address_country",
              "billing_phone_number",
              "customer_name",
              "customer_email_address",
              "ordering_session_id",
              "website_url")

    val fields = recordElements.split('\t').map(_.trim)

    //val jsonString = new Gson().toJson((header zip fields)).toString
    //println(jsonString)
    val newOrder = new Order(new BigInteger(fields(0)), fields(9).toFloat, fields(10).toFloat)

    val jsonString = new Gson().toJson(newOrder).toString
    println(jsonString)

    return jsonString
  }

  def postToGemFireREST(record: String, locatorHost: String, regionName: String): Unit = {
    //println("begin foreach")
    var restPort = 28080

    //println("before http client")

    val client = new DefaultHttpClient

    //record.foreach { el =>
    //"curl -H 'Content-Type: application/json' -X POST 10.80.2.172:28080/gemfire-api/v1/{region}?key={key} -d'{data}' "

    var thisOrder: String = null
    try {
      val rowElements = record.split('\t').map(_.trim)

      thisOrder = rowElements(0)
    } catch {
      case e: java.util.NoSuchElementException => println(record)
    }

    if (thisOrder != null) {

      val gemfireUrl = "http://" + locatorHost + ":" + restPort + "/gemfire-api/v1/" + regionName + "?key=" + thisOrder
      //println(gemfireUrl)

      val post = new HttpPost(gemfireUrl)
      post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      post.setEntity(new StringEntity(makeOrderJsonString(record))) //new Gson().toJson(rowElements(2))))

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
  def gfProcessStream(stream: DStream[String], locatorHost: String, locatorPort: Integer, regionName: String, embeddedServerPort: Integer, isTest: Boolean): Unit = {

    stream.foreachRDD { rdd =>
      //println("begin foreachRDD")
      //if (rdd != null) {
        //println("begin foreachPartition - rdd not null")
        rdd.foreachPartition { records =>
          //if (records != null) {
            records.foreach( record => postToGemFireREST(record, locatorHost, regionName) )
          //}
        }
      //}
    }
  }

  def gfProcessOrderCsv(stream: DStream[String], locatorHost: String, locatorPort: Integer, regionName: String, embeddedServerPort: Integer, isTest: Boolean): Unit = {

    stream.foreachRDD { rdd =>
      rdd.foreachPartition { record =>
        var region: Region[BigInteger, Order] = null

        if (isTest) {
          region = gemfire.embeddedRegion(locatorHost + "[" + locatorPort + "]", embeddedServerPort, regionName).asInstanceOf[Region[BigInteger, Order]]
        }

        record.foreach { el =>
          val rowElements = el.split('\t').map(_.trim)

          //rowElements.foreach(println)

          var thisOrder = new Order(new BigInteger(rowElements(0)))

          region.put(thisOrder.getOrder_id(), thisOrder)
          //println(thisOrder.toString)
        }
      }
    }
  }
}

package io.pivotal.sample

import java.util.Properties
import java.util.concurrent.TimeUnit

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

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import io.pivotal.pde.Order
import io.pivotal.pde.OrderLineItem

/*
Encapsulating the GemFire objects in a single container object allows them to
be serialized correctly for use on the Spark workers, workaround credit to:
https://www.nicolaferraro.me/2016/02/22/using-non-serializable-objects-in-apache-spark/
 */
object gemfire {

  /*
  The following are used for the production "profile"
   */
  def setupGemfire(locatorHost: String, locatorPort: Integer): ClientCache = {
    var gemfireProperties = new Properties()
    gemfireProperties.setProperty("log-level", System.getProperty("gemfire.log.level", "debug"))

    return new ClientCacheFactory(gemfireProperties)
      .addPoolLocator(System.getProperty("gemfire.cache.server.host", locatorHost),
        Integer.getInteger("gemfire.cache.server.port", locatorPort))
      .create()
  }

  def setupRegion(cache: ClientCache, regionName: String): Region[String, String] = {
    return cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName)
  }

  def region(locatorHost: String, locatorPort: Integer, regionName: String) = {
    var clientCache = setupGemfire(locatorHost, locatorPort)
    val region = setupRegion(clientCache, regionName)

    region
  }

  /*
  The following are used for the dev/test "profile"
   */
  var embRegion: Region[String, String] = null
  var embCache: Cache = null

  def setupGemfireEmbedded(embeddedLocatorHostPort: String, embeddedServerPort: Integer): Cache = {
    var gemfireProperties = new Properties()
    gemfireProperties.setProperty("log-level", System.getProperty("gemfire.log.level", "config"))
    gemfireProperties.setProperty("start-locator", System.getProperty("gemfire.locator.host-port", embeddedLocatorHostPort));

    var cf = new CacheFactory(gemfireProperties)
      .set("locators", embeddedLocatorHostPort)

    var c = cf.create()

    var cs = c.addCacheServer()
    cs.setPort(embeddedServerPort)
    cs.start()

    embCache = c

    return c
  }

  def setupEmbeddedRegion(cache: Cache, regionName: String): Region[String, String] = {
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
    val conf = new SparkConf().setMaster(args(0)).setAppName("PushToGemFireApp")
    val sparkContext = new SparkContext(conf)
    val ssc = new StreamingContext(conf, Seconds(5))

    val rdd = sparkContext.textFile("/tmp/testfile")
    val rddQueue: Queue[RDD[String]] = Queue()
    rddQueue += rdd
    //rdd.print()

    //clientCache = setupGemfire("172.16.139.1", 10334)
    //orderRegion = setupRegion(clientCache, "Orders")

    val stream = ssc.queueStream(rddQueue)
    stream.print()

    processStream(args, stream)

    ssc.start()
    ssc.awaitTermination()

  }

  def processStream(args: Array[String], stream: DStream[String]): Unit = {
    args match {
      case Array(_, _, path, _*) => stream.saveAsTextFiles(args(2))
      case _ => return
    }
  }

  // executed at the worker
  def gfProcessStream(stream: DStream[String], locatorHost: String, locatorPort: Integer, regionName: String, embeddedServerPort: Integer, isTest: Boolean): Unit = {

    stream.foreachRDD { rdd =>
      rdd.foreachPartition { record =>
        var region: Region[String, String] = null

        if (isTest) {
          region = gemfire.embeddedRegion(locatorHost + "[" + locatorPort + "]", embeddedServerPort, regionName)
        }
        else{
          region = gemfire.region(locatorHost, locatorPort, regionName)
        }

        record.foreach { el =>
          region.put(el.toString, el.toString)
          println(el.toString)
        }
      }
    }
  }

  def gfProcessOrderCsv(stream: DStream[String], locatorHost: String, locatorPort: Integer, regionName: String, embeddedServerPort: Integer, isTest: Boolean): Unit = {

    stream.foreachRDD { rdd =>
      rdd.foreachPartition { record =>
        var region: Region[Integer, Order] = null

        if (isTest) {
          region = gemfire.embeddedRegion(locatorHost + "[" + locatorPort + "]", embeddedServerPort, regionName)
        }
        else{
          region = gemfire.region(locatorHost, locatorPort, regionName)
        }

        record.foreach { el =>
          val rowElements = el.map(line => line.split('\t').map(_.trim))

          var thisOrder = new io.pivotal.pde.Order(rowElements(0))

          region.put(el.toString, el.toString)
          println(el.toString)
        }
      }
    }
  }
}

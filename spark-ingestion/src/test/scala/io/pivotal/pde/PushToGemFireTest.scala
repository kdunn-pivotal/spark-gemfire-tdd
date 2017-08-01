package io.pivotal.sample

import java.util.Properties

import io.pivotal.sample.PushToGemFireApp.{gfProcessStream, processStream}

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

import org.apache.hadoop.mapred.InvalidInputException
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{ClockWrapper, Seconds, StreamingContext}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.reflect.io.Path
import scala.util.Try

class PushToGemFireTest extends FlatSpec with Matchers with Eventually with BeforeAndAfter with BeforeAndAfterAll {

  private val SPARK_MASTER = "local[*]" //"spark://Kyle-Dunn-MacBook-Pro.local:7077"
  private val APP_NAME = "PushToGemFireAppTest"
  private val FILE_PATH: String = "target/testfile"
  private val EMBEDDED_GEMFIRE_LOCATOR_HOST = "192.168.69.1"
  private val EMBEDDED_GEMFIRE_LOCATOR_PORT = 20334
  private val EMBEDDED_GEMFIRE_REST_PORT = "38080"

  private var ssc: StreamingContext = _

  private val batchDuration = Seconds(1)

  var clock: ClockWrapper = _

  before {
    val conf = new SparkConf()
      .setMaster(SPARK_MASTER).setAppName(APP_NAME)
      .set("spark.streaming.clock", "org.apache.spark.streaming.util.ManualClock")

    ssc = new StreamingContext(conf, batchDuration)
    clock = new ClockWrapper(ssc)
  }

  after {
    // Stop the Spark Streaming context
    if (ssc != null) ssc.stop()

    Try(Path(FILE_PATH + "-1000").deleteRecursively)
  }

  override def afterAll() {
    if (gemfire.embCache != null) gemfire.embCache.close(false)
  }


  /*
      The following objects are used for the dev/test "profile"

      Encapsulating the GemFire objects in a single container object allows them to
      be serialized correctly for use on the Spark workers, workaround credit to:
      https://www.nicolaferraro.me/2016/02/22/using-non-serializable-objects-in-apache-spark/
   */
  object gemfire {

    var embRegion: Region[_, _] = null
    var embCache: Cache = null

    def setupGemfireEmbedded(embeddedLocatorHostPort: String, embeddedServerPort: Integer): Cache = {
      var gemfireProperties = new Properties()
      gemfireProperties.setProperty("log-level", System.getProperty("gemfire.log.level", "config"))
      gemfireProperties.setProperty("start-locator", System.getProperty("gemfire.locator.host-port", embeddedLocatorHostPort));
      gemfireProperties.setProperty("start-dev-rest-api", System.getProperty("gemfire.start-dev-rest-api", "true"));
      gemfireProperties.setProperty("http-service-port", System.getProperty("gemfire.http-service-port", EMBEDDED_GEMFIRE_REST_PORT));

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

  "PushToGemFire Streaming App " should " store streams into a GemFire region" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()

    val rangeStart = 1025
    val rangeEnd = 65534
    val rnd = new scala.util.Random
    val randomRegion = "SimpleTest-" + rnd.nextInt()

    val cache = gemfire.embeddedRegion(EMBEDDED_GEMFIRE_LOCATOR_HOST + "[" + EMBEDDED_GEMFIRE_LOCATOR_PORT + "]", 40404, randomRegion)

    gfProcessStream(dstream, EMBEDDED_GEMFIRE_LOCATOR_HOST, EMBEDDED_GEMFIRE_LOCATOR_PORT, randomRegion, EMBEDDED_GEMFIRE_REST_PORT)

    ssc.start()

    lines += ssc.sparkContext.makeRDD(List("a", "b", "c", "d"))

    clock.advance(1000)

    eventually(timeout(2 seconds)){
      gemfire.embRegion.size() should be (4)
    }
  }

  "PushToGemFire Streaming App " should " store Orders from a CSV into a GemFire region via REST" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()

    val randomRegion = "Orders"

    val cache = gemfire.embeddedRegion(EMBEDDED_GEMFIRE_LOCATOR_HOST + "[" + EMBEDDED_GEMFIRE_LOCATOR_PORT + "]", 40404, randomRegion)

    gfProcessStream(dstream, EMBEDDED_GEMFIRE_LOCATOR_HOST, EMBEDDED_GEMFIRE_LOCATOR_PORT, randomRegion, EMBEDDED_GEMFIRE_REST_PORT)

    ssc.start()

    lines += ssc.sparkContext.textFile("/Users/kdunn/gdrive/SampleData/retail_demo/orders/sample_orders.tsv.gz")

    clock.advance(1000)

    eventually(timeout(1 seconds)){
      gemfire.embRegion.size() should be (10)
    }
  }

  "PushToGemFire Streaming App " should " store OrderLineItems from a CSV into a GemFire region via REST" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()

    val randomRegion = "OrderLineItems"

    val cache = gemfire.embeddedRegion(EMBEDDED_GEMFIRE_LOCATOR_HOST + "[" + EMBEDDED_GEMFIRE_LOCATOR_PORT + "]", 40404, randomRegion)

    gfProcessStream(dstream, EMBEDDED_GEMFIRE_LOCATOR_HOST, EMBEDDED_GEMFIRE_LOCATOR_PORT, randomRegion, EMBEDDED_GEMFIRE_REST_PORT)

    ssc.start()

    lines += ssc.sparkContext.textFile("/Users/kdunn/gdrive/SampleData/retail_demo/order_lineitems/sample_order_lineitems.tsv.gz")

    clock.advance(1000)

    eventually(timeout(1 seconds)){
      gemfire.embRegion.size() should be (10)
    }
  }

  "PushToGemFire Streaming App " should " store empty streams if no data received" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()
    processStream(Array("", "", FILE_PATH), dstream)
    
    ssc.start()

    clock.advance(1000)

    eventually(timeout(1 seconds)){
      val wFile: RDD[String] = ssc.sparkContext.textFile(FILE_PATH+ "-1000")
      wFile.count() should be (0)
      wFile.collect().foreach(println)
    }

  }

  "PushToGemFire Streaming App " should " not store streams if argument is not passed" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()
    processStream(Array("", ""), dstream)

    val wFile: RDD[String] = ssc.sparkContext.textFile(FILE_PATH + "-1000")

    ssc.start()

    lines += ssc.sparkContext.makeRDD(Seq("b", "c"))
    clock.advance(1000)

    eventually(timeout(1 seconds)) {
      a[InvalidInputException] should be thrownBy {
        wFile.count() should be(0)
      }
    }
  }

}

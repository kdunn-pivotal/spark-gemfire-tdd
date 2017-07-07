package io.pivotal.sample

import io.pivotal.sample.PushToGemFireApp._

import org.apache.geode.cache.Cache
import org.apache.geode.cache.DataPolicy
import org.apache.geode.cache.Region
import org.apache.geode.cache.client.ClientCache
import org.apache.geode.cache.RegionShortcut
import org.apache.geode.cache.RegionExistsException
import org.apache.geode.cache.client.ServerOperationException

import org.apache.hadoop.mapred.InvalidInputException

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{ClockWrapper, Seconds, StreamingContext}

import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

import java.util.Properties
import java.util.concurrent.TimeUnit

import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.reflect.io.Path
import scala.util.Try

class PushToGemFireTest extends FlatSpec with Matchers with Eventually with BeforeAndAfterAll with BeforeAndAfter {

  private val SPARK_MASTER = "local[3]" //"spark://Kyle-Dunn-MacBook-Pro.local:7077" //"local[*]"
  private val APP_NAME = "PushToGemFireAppTest"
  private val FILE_PATH: String = "target/testfile"
  private val EMBEDDED_GEMFIRE_LOCATOR_HOST = "192.168.69.1" // "172.16.139.1"
  private val EMBEDDED_GEMFIRE_LOCATOR_PORT = 20334
  private val EMBEDDED_GEMFIRE_SERVER_PORT = 40404

  private var ssc: StreamingContext = _

  private var cache: Cache = null
  private var region: Region[String, String] = null

  private val batchDuration = Seconds(1)

  var clock: ClockWrapper = _

  override def beforeAll() {
    //cache = gemfire.setupGemfireEmbedded(EMBEDDED_GEMFIRE_LOCATOR_HOST + "[" + EMBEDDED_GEMFIRE_LOCATOR_PORT + "]", EMBEDDED_GEMFIRE_SERVER_PORT)
    //val gemfire.embeddedRegion = setupEmbeddedRegion(cache, regionName)
  }

  before {
    val conf = new SparkConf()
      .setMaster(SPARK_MASTER).setAppName(APP_NAME)
      .set("spark.streaming.clock", "org.apache.spark.streaming.util.ManualClock")

    ssc = new StreamingContext(conf, batchDuration)
    clock = new ClockWrapper(ssc)

    //cache = gemfire.setupGemfireEmbedded(EMBEDDED_GEMFIRE_LOCATOR_HOST + "[" + EMBEDDED_GEMFIRE_LOCATOR_PORT + "]", EMBEDDED_GEMFIRE_SERVER_PORT)
    //val gemfire.embeddedRegion = setupEmbeddedRegion(cache, regionName)
  }

  after {
    // Close the connection to GemFire
    if (gemfire.embCache != null) gemfire.embCache.close(false)

    // Stop the Spark Streaming context
    if (ssc != null) ssc.stop()

    Try(Path(FILE_PATH + "-1000").deleteRecursively)
  }

  "PushToGemFire Streaming App " should " store streams into a GemFire region" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()

    val rangeStart = 1025
    val rangeEnd = 65534
    val rnd = new scala.util.Random
    val randomRegion = "OrdersTest-" + rnd.nextInt()
    val randomPort = rangeStart + rnd.nextInt(rangeEnd - rangeStart)
    //region = gemfire.setupEmbeddedRegion(cache, randomRegion)

    gfProcessStream(dstream, EMBEDDED_GEMFIRE_LOCATOR_HOST, randomPort, randomRegion, randomPort + 1, true)

    ssc.start()


    lines += ssc.sparkContext.makeRDD(List("b", "c")).repartition(1)
    clock.advance(1000)

    var region: Region[String, String] = null
    eventually(timeout(30 seconds)){
      region = gemfire.embRegion

      //region.put("b", "b")
      //region.put("c", "c")

      //val wFile: RDD[String] = ssc.sparkContext.textFile(FILE_PATH+ "-1000")
      region.size() should be (2)
    }

    //if (region != null) region.close()
    if (gemfire.embCache != null) gemfire.embCache.close(false)
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
    clock.advance(2000)

    eventually(timeout(3 seconds)) {
      a[InvalidInputException] should be thrownBy {
        wFile.count() should be(0)
      }
    }
  }

  }

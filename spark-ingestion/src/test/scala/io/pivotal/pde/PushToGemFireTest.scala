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

class PushToGemFireTest extends FlatSpec with Matchers with Eventually with BeforeAndAfter {

  private val SPARK_MASTER = "local[3]" //"spark://Kyle-Dunn-MacBook-Pro.local:7077" //"local[*]"
  private val APP_NAME = "PushToGemFireAppTest"
  private val FILE_PATH: String = "target/testfile"
  private val EMBEDDED_GEMFIRE_LOCATOR_HOST = "172.16.139.1"
  private val EMBEDDED_GEMFIRE_LOCATOR_PORT = 20334
  private val EMBEDDED_GEMFIRE_SERVER_PORT = 40404

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
    // Close the connection to GemFire
    if (gemfire.embCache != null) gemfire.embCache.close(false)

    // Stop the Spark Streaming context
    if (ssc != null) ssc.stop()

    Try(Path(FILE_PATH + "-1000").deleteRecursively)
  }

  /*
  "PushToGemFire Streaming App " should " store streams into a GemFire region" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()

    val rangeStart = 1025
    val rangeEnd = 65534
    val rnd = new scala.util.Random
    val randomRegion = "SimpleTest-" + rnd.nextInt()
    //val randomPort = rangeStart + rnd.nextInt(rangeEnd - rangeStart)

    gfProcessStream(dstream, EMBEDDED_GEMFIRE_LOCATOR_HOST, EMBEDDED_GEMFIRE_LOCATOR_PORT, randomRegion, EMBEDDED_GEMFIRE_SERVER_PORT, true)

    ssc.start()

    // Override the number of partitions to avoid a testing deadlock
    // due to insufficient amount of data for each Spark worker
    lines += ssc.sparkContext.makeRDD(List("b", "c")).repartition(1)

    clock.advance(1000)

    eventually(timeout(5 seconds)){
      //region = gemfire.embRegion

      gemfire.embRegion.size() should be (2)

      gemfire.embRegion.close()
    }
  }
  */

  "PushToGemFire Streaming App " should " store Orders in a CSV into a GemFire region" in {
    val lines = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines)

    dstream.print()

    val rangeStart = 1025
    val rangeEnd = 65534
    val rnd = new scala.util.Random
    val randomRegion = "OrdersTest-" + rnd.nextInt()
    //val randomPort = rangeStart + rnd.nextInt(rangeEnd - rangeStart)

    gfProcessOrderCsv(dstream, EMBEDDED_GEMFIRE_LOCATOR_HOST, EMBEDDED_GEMFIRE_LOCATOR_PORT, randomRegion, EMBEDDED_GEMFIRE_SERVER_PORT, true)

    ssc.start()

    // Override the number of partitions to avoid a testing deadlock
    // due to insufficient amount of data for each Spark worker
    lines += ssc.sparkContext.textFile("/Users/kdunn/gdrive/SampleData/retail_demo/orders/sample_orders.tsv.gz").repartition(1)

    clock.advance(10000)

    eventually(timeout(15 seconds)){
      //region = gemfire.embRegion

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

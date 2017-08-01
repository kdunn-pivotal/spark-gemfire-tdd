package io.pivotal.sample

import java.util.ArrayList
import java.math.BigInteger
import java.util.concurrent.TimeUnit

import scala.collection.mutable.Queue

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
import org.apache.http.entity.StringEntity
import com.google.gson.Gson

import io.pivotal.pde.model.Order
import io.pivotal.pde.model.OrderLineItem

/**
  * Spark Streaming with automated tests
  */
object PushToGemFireApp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("PushToGemFireApp")

    val ssc = new StreamingContext(conf, Seconds(5))

    val orderRdd = ssc.sparkContext.textFile("file:///Users/kdunn/gdrive/SampleData/retail_demo/orders/orders.tsv.gz")
    val orderRddQueue: Queue[RDD[String]] = Queue()
    orderRddQueue += orderRdd

    val orderStream = ssc.queueStream(orderRddQueue)

    val orderLineItemRdd = ssc.sparkContext.textFile("file:///Users/kdunn/gdrive/SampleData/retail_demo/order_lineitems/sample_order_lineitems.tsv.gz")
    val orderLineItemRddQueue: Queue[RDD[String]] = Queue()
    orderLineItemRddQueue += orderLineItemRdd

    val orderLineItemStream = ssc.queueStream(orderLineItemRddQueue)

    gfProcessStream(orderStream, "172.16.139.1", 10334, "Orders", "28080")

    gfProcessStream(orderLineItemStream, "172.16.139.1", 10334, "OrderLineItems", "28080")

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

      //"curl -H 'Content-Type: application/json' -X POST 10.80.2.172:28080/gemfire-api/v1/{region}?key={key} -d'{data}' "
      val gemfireUrl = "http://" + locatorHost + ":" + restPort + "/gemfire-api/v1/" + regionName + "?key=" + thisKey

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
  def gfProcessStream(stream: DStream[String], locatorHost: String, locatorPort: Integer, regionName: String, restPort: String): Unit = {
    stream.foreachRDD { rdd =>
      rdd.foreachPartition { records =>
          records.foreach( record => postToGemFireREST(record, locatorHost, restPort, regionName) )
      }
    }
  }

}

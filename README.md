# Spark GemFire AEQ sample

This project demonstrates how to use Spark to ingest into Pivotal GemFire via the Java API.  The process is as follows:
1. Read two CSV files from disk using Spark's `textFile`
2. Iterate over the records with a Spark `foreachPartition` operation
   - Instantiate `Order` and `OrderLineItem` objects from the records
   - Store the object into the respective GemFire region
3. GemFire asynchronously appends (trickle merges) `OrderLineItems` into the corresponding `Order` objects, based on the `order_id` field
4. A Spring Boot GemFire client receives realtime updates to `Order` objects and displays them in a UI


### Data Source

Please see [this](https://github.com/pivotalsoftware/pivotal-samples/tree/master/sample-data) repo for the `retail_demo` sample data.

### Build

    $ sbt assembly

### Run

    $ (cd $SPARK_HOME && ./sbin/start-all.sh)

    $ $SPARK_HOME/bin/spark-submit target/scala-2.11/spark-streaming-tests_2.11-1.0.jar spark://Kyle-Dunn-MacBook-Pro.local:7077
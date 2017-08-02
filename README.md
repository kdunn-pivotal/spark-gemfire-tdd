# Spark GemFire AEQ sample

This project demonstrates how to use Spark to ingest into Pivotal GemFire via the REST API.  The process is as follows:
1. Read two CSV files from disk using Spark's `textFile`
2. Iterate over the records with a Spark `foreachPartition` operation
   - Instantiate `Order` and `OrderLineItem` objects from the records
   - Store the object into the respective GemFire region
3. GemFire asynchronously appends (trickle merges) `OrderLineItems` into the corresponding `Order` objects, based on the `order_id` field
4. A Spring Boot GemFire client receives realtime updates to `Order` objects and displays them in a UI

(note: the GemFire Java API was also successfully tested but abandoned due to added complexity)

### Data Source

Please see [this](https://github.com/pivotalsoftware/pivotal-samples/tree/master/sample-data) repo for the `retail_demo` sample data.

### Build Spark ingestion

    $ (cd spark-ingestion && sbt assembly)

### Build Java components (domain model, GemFire server-side functions, webapp)

    $ mvn package

### Start GemFire cluster

    $ (cd gemfire-server/scripts && ./startCluster.gfsh)

### Start webapp UI

    $ java -jar spring-websocket-reactive-app/target/spring-websocket-reactive-1.0-SNAPSHOT.jar 

### Submit Spark ingestion application

    $ $SPARK_HOME/bin/spark-submit --class "io.pivotal.sample.PushToGemFireApp"  --master local[*] spark-ingestion/target/scala-2.11/spark-gemfire-ingestion-assembly-1.0.jar file:///Users/kdunn/gdrive/SampleData/retail_demo/orders/10k_orders.tsv.gz file:///Users/kdunn/gdrive/SampleData/retail_demo/order_lineitems/10k_order_lineitems.tsv.gz

# Spark-Gemfire Ingestion via Scala

This module handles reading two delimited files from disk and writing the contents via a Spark `foreachPatition` to two GemFire regions using the GemFire REST API: 
- `Orders`
- `OrderLineItems`
  

### pom.xml 

To regenerate the pom.xml from the `build.sbt`, run the following:

     $ sbt make-pom

Note, you may need to re-add the GemFire commercial repository afterwards.

### Test

     $ sbt assembly 
     $ $SPARK_HOME/bin/spark-submit --class "io.pivotal.sample.PushToGemFireApp"  --master local[8] target/scala-2.11/spark-gemfire-ingestion-assembly-1.0.jar

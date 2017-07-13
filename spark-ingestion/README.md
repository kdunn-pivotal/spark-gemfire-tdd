# Spark-Gemfire Ingestion via Scala

This module handles reading a delimited file from disk and writing the contents via a Spark `foreachPatition` to two GemFire regions using the Java API: 
- `Orders`
- `OrderLineItems`
  

### pom.xml 

To regenerate the pom.xml from the `build.sbt`, run the following:

     $ sbt make-pom

Note, you may need to re-add the GemFire commercial repository afterwards.
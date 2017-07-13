# Spark GemFire AEQ sample

### Data Source

Please see [this](https://github.com/pivotalsoftware/pivotal-samples/tree/master/sample-data) repo for the `retail_demo` sample data.

### Build

    $ sbt assembly

### Run

    $ (cd $SPARK_HOME && ./sbin/start-all.sh)

    $ $SPARK_HOME/bin/spark-submit target/scala-2.11/spark-streaming-tests_2.11-1.0.jar spark://Kyle-Dunn-MacBook-Pro.local:7077
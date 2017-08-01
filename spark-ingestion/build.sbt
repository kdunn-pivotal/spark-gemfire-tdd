name := "spark-gemfire-ingestion"
 
version := "1.0"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

excludedJars in assembly <<= (fullClasspath in assembly) map { cp => 
  cp filter { 
        i => i.data.getName == "slf4j-api-1.7.12.jar" 
        }
}
 
scalaVersion := "2.11.7"
 
resolvers += "jitpack" at "https://jitpack.io"
resolvers += Resolver.mavenLocal

addCommandAlias("sanity", ";clean ;compile ;coverage ;test; coverageReport")

// Apache Geode provides a newer version of Jackson; override Spark's transitive depencency on v2.7.3
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.2"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.2"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.2"
 
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.1.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.1.1" % "provided",
  "com.google.code.gson" % "gson" % "2.8.1",
  "org.apache.httpcomponents" % "httpclient" % "4.5.3",
  "org.apache.logging.log4j" % "log4j-core" % "2.6.1" % "provided",
  "io.pivotal.gemfire" % "geode-core" % "9.0.3",
  "io.pivotal.pde" % "retail-sample-model" % "1.0-SNAPSHOT",
// comment above line and uncomment the following to run in sbt
// "org.apache.spark" %% "spark-streaming" % "1.6.1",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.jfarcand" % "wcs" % "1.5",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.scala-lang" % "scala-library" % "2.11.7"
)

// Apache Geode provides log4j-core v2.6.1; avoid duplicate dependencies
assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}

// GemFire embedded singleton needs tests to be run sequentially
parallelExecution in Test := false

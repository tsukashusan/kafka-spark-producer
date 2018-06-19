import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val spakCore = "org.apache.spark" % "spark-cloud_2.11" % "2.2.0.3.0.0.3-2" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakSQL = "org.apache.spark" % "spark-sql_2.11" % "2.2.0.3.0.0.3-2" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakStreaming = "org.apache.spark" % "spark-streaming_2.11" % "2.2.0.3.0.0.3-2" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakMlib = "org.apache.spark" % "spark-mllib_2.11" % "2.2.0.3.0.0.3-2" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val sparkKafka = "org.apache.spark" % "spark-sql-kafka-0-10_2.11" % "2.2.0.3.0.0.3-2" exclude("org.apache.zookeeper", "zookeeper")
}

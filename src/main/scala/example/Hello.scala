package example

import org.apache.spark.sql.SparkSession

object Hello extends Greeting with App {
  println(greeting)

  final val SPARK = SparkSession.builder.appName("kafkaSparkclient").getOrCreate()
  
  if (args.length != 3) {
    usage()
    sys.exit
  }else{
    run(SPARK, args)
  }


  def usage(): Unit = {
    println("Usage:");
    println("kafka-spark-producer.jar <batch|stream> brokerhosts topic");
    sys.exit(1);
  }


  def run(spark: SparkSession, args: Array[String]): Unit = {
    import java.util.UUID
    val brokers = args(1)
    var topic = args(2)
    args(0) match {
      case "batch" => batch(spark, brokers, topic)
      case "stream" =>stream(spark, brokers, topic)
      case _ => usage
    }
    sys.exit
  }


  def batch(spark: SparkSession, kafkaBrokers: String, kafkaTopic: String):Unit = {
    // Read a batch from Kafka
    import spark.implicits._
    import org.apache.spark.sql.functions.from_json
    import org.apache.spark.sql.functions.col
    import org.apache.spark.sql.types.StructType
    //import org.apache.spark.sql.types.TimestampType
    //case class DeviceInfo(deviceId: String, temperature: Double, humidity: Double)
    //{"deviceId":"raspberrypi3","temperature":21.275536678011267,"humidity":78.00421342575244}
    val schema = new StructType()
      .add($"deviceId".string)
      .add($"offset".int)
      .add($"contentType".string)
      .add($"enqueuedTime".string)
      .add($"sequenceNumber".long)
      .add($"content".string)
      .add("systemProperties", (new StructType())
        .add($"correlation-id".string)
        .add($"iothub-message-source".string)
        .add($"iothub-enqueuedtime".string)
        .add($"message-id".string)
        .add($"iothub-connection-auth-generation-id".long)
        .add($"iothub-connection-auth-method".string)
      )
      .add($"properties".string)

    val kafkaDF = spark.read.format("kafka")
                    .option("kafka.bootstrap.servers", kafkaBrokers)
                    .option("subscribe", kafkaTopic)
                    .option("startingOffsets", "earliest")
                    .load()
    // Select data and write to file
    kafkaDF.select(from_json(col("value").cast("string"), schema) as "iotsample")
                    .write
                    .format("json")
                    .option("path","/example/batchtripdata")
                    .option("checkpointLocation", "/batchcheckpoint")
                    .save()
  }


  def stream(spark: SparkSession, kafkaBrokers: String, kafkaTopic: String):Unit = {
    // Stream from Kafka
    import spark.implicits._
    import org.apache.spark.sql.functions.from_json
    import org.apache.spark.sql.functions.col
    import org.apache.spark.sql.types.StructType
    //import org.apache.spark.sql.types.TimestampType
    val schema = new StructType()
      .add($"deviceId".string)
      .add($"offset".int)
      .add($"contentType".string)
      .add($"enqueuedTime".string)
      .add($"sequenceNumber".long)
      .add($"content".string)
      .add("systemProperties", (new StructType())
        .add($"correlation-id".string)
        .add($"iothub-message-source".string)
        .add($"iothub-enqueuedtime".string)
        .add($"message-id".string)
        .add($"iothub-connection-auth-generation-id".long)
        .add($"iothub-connection-auth-method".string)
      )
      .add($"properties".string)
    val kafkaStreamDF = spark.readStream.format("kafka")
                    .option("kafka.bootstrap.servers", kafkaBrokers)
                    .option("subscribe", kafkaTopic)
                    .option("startingOffsets", "earliest")
                    .load()
    // Select data from the stream and write to file
    kafkaStreamDF.select(from_json(col("value").cast("string"), schema) as "trip")
                    .writeStream
                    .format("parquet")
                    .option("path","/example/streamingtripdata")
                    .option("checkpointLocation", "/streamcheckpoint")
                    .start.awaitTermination(30000)
  }

  def testRead(): Unit ={
    
  }
}

trait Greeting {
  lazy val greeting: String = "hello"
}

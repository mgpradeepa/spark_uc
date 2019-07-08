package com.p.mgp.kss.interfaces

import java.util.Properties

import com.p.mgp.kss.clients.kafka.GenericKafkaClient
import com.p.mgp.kss.data.avro.KSSAvroSchema
import com.p.mgp.kss.serverutils.EmbeddedKafkaServer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.avro.generic.GenericContainer

object KSStreamPrimaryAvroInterface {

    def main (args: Array[String]): Unit ={
      val kafkaTopic = "ImpDATA"

      val kafkaServer = EmbeddedKafkaServer()
      kafkaServer.start()
      kafkaServer.createTopic(kafkaTopic,2)


      val conf = new SparkConf().setAppName("MGP_DataStreaming").setMaster("local")
      val sc = new SparkContext(conf)

      // consider the stream is going to send data every second
      val ssc = new StreamingContext(sc, Seconds(1))

      val props : Properties = GenericKafkaClient.avroConsumer(kafkaServer)

      // check how to obtain the consumer.
//      private val consumerConnector = Consumer.create(props)

      val kafkaStream = KafkaUtils.createDirectStream(
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[Long, GenericContainer ](java.util.Arrays.asList(kafkaTopic), props.asInstanceOf[java.util.Map[String, Object]]))

      // start streaming
      ssc.start()

      println("Started streaming in a termination monitor")

      //sleep for some time buddy
      Thread.sleep(5000)

      // now start the producer context
      val producerThread = new Thread("Producer terminal" ){
        override  def run(): Unit = {
          val myClient = new GenericKafkaClient(kafkaServer)

          myClient.sendAvroData(kafkaTopic,1L, KSSAvroSchema.populateSampleRecord)

          Thread.sleep(500)
          println("*** terminate streaming context")

          ssc.stop(stopSparkContext = false, stopGracefully = true)
        }
      }
      producerThread.start()

      try {
        ssc.awaitTermination()
        println("Streaming terminated")
      }catch {
        case e :Exception =>
          println("*** Exception of streaming caught in monitor thread")

      }
      // stop spark
      sc.stop()


      // stop kafka
      kafkaServer.stop()

      println("Streaming and parsing accomplished")
    }

  }

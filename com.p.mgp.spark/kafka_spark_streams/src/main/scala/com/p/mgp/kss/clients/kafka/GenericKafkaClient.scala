package com.p.mgp.kss.clients.kafka

import java.util.Properties
import scala.collection.JavaConverters._

import com.p.mgp.kss.serverutils.EmbeddedKafkaServer
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

case class GenericKafkaClient(kafkaServer : EmbeddedKafkaServer) {

  private val LOGGER = Logger[GenericKafkaClient]
  // sender or producer client

  def send(topic : String, data : Seq[(String,String)]) : Unit = {
    val producer = new KafkaProducer[String, String ](stringsProducer)
    data.foreach(d => {
      producer send new ProducerRecord(topic, d._1, d._2)
    })
    producer.close()
  }

  def consumeToConsole(topic:String , maxCount: Int) :Unit ={
    // config a consumer
    val consumer = new KafkaConsumer[String, String ](consumeStrings)

    // subscribing to a topic
    consumer.subscribe(java.util.Arrays.asList(topic))

    var count =0

    while(count < maxCount){
      LOGGER.info("Polling")

      val records :ConsumerRecords[String, String] = consumer.poll(100)
      LOGGER.info("recieved {} so many messages",records.count())

      count = count + records.count()

       for(rec <- records.records(topic).asScala) {
         println("*** [ " + rec.partition() + " ] " + rec.key() + ":" + rec.value())
       }

    }
    println("*** got the expected number of messages")

    consumer.close()
  }

  def stringsProducer :Properties = {
    val config :Properties = new Properties
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , classOf[StringSerializer].getCanonicalName)
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer.getKafkaConnect)
    config
  }

  def consumeStrings :Properties = {
    GenericKafkaClient.stringsConsumer(kafkaServer)
  }
}

object GenericKafkaClient {


  def stringsConsumer(server: EmbeddedKafkaServer, group: String ="MGP_GROUP") : Properties  = {
    val consumerConfig : Properties = new Properties
    consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, group)
    consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
    consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server.getKafkaConnect)
    consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    consumerConfig
  }

}

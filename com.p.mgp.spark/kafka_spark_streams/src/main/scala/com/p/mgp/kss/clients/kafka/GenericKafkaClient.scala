package com.p.mgp.kss.clients.kafka

import java.util.Properties

import com.p.mgp.kss.data.avro.{AvroGenericRecordSerializer, LongSerializer}
import com.p.mgp.kss.data.variety.KafkaData
import com.p.mgp.kss.serverutils.EmbeddedKafkaServer
import com.typesafe.scalalogging.Logger
import org.apache.avro.generic.GenericContainer
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, LongDeserializer, StringDeserializer, StringSerializer}

import scala.collection.JavaConverters._

case class GenericKafkaClient(kafkaServer : EmbeddedKafkaServer) {

  private val LOGGER = Logger[GenericKafkaClient]
  // sender or producer client

  def send(topic : String, data : Seq[KafkaData]) : Unit = {
    val producer = new KafkaProducer[String, String ](stringsProducer)
    data.foreach(d => {
      producer send new ProducerRecord(topic, d.key, d.value.toString)
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

//      records.records(topic).forEach(rec => {
//        LOGGER.info("*** [ " + rec.partition() + " ] " + rec.key() + ":" + rec.value())
//      })
       for(rec <- records.records(topic).asScala) {
         LOGGER.info("*** [ " + rec.partition() + " ] " + rec.key() + ":" + rec.value())
       }

    }
    LOGGER.info("*** got the expected number of messages")

    consumer.close()
  }

  def stringsProducer :Properties = {
    val config :Properties = new Properties
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , classOf[StringSerializer].getCanonicalName)
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer.getKafkaConnect)
    config
  }
  def avroProducer :Properties = {
    val config :Properties = new Properties
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , classOf[LongSerializer].getCanonicalName)
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[AvroGenericRecordSerializer].getCanonicalName)
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer.getKafkaConnect)
    config
  }

  def consumeStrings :Properties = {
    GenericKafkaClient.stringsConsumer(kafkaServer)
  }

  def sendAvroData(topic : String, id: Long,  data : GenericContainer) : Unit = {
    val producer = new KafkaProducer[Long, GenericContainer](avroProducer)
    producer send new ProducerRecord(topic,id, data)
    producer.close()
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

  def avroConsumer (server: EmbeddedKafkaServer, group: String = "MGP_GROUP") :Properties = {
    val consumerConfig : Properties = new Properties
    consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, group)
    consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[LongDeserializer].getCanonicalName)
    consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[ByteArrayDeserializer].getCanonicalName)
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server.getKafkaConnect)
    consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    consumerConfig
  }

}

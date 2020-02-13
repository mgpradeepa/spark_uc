package com.p.mgp.kss.interfaces

import com.p.mgp.kss.clients.kafka.GenericKafkaClient
import com.p.mgp.kss.data.variety.{KafkaData, KafkaDataAsStringValue}
import com.p.mgp.kss.serverutils.EmbeddedKafkaServer

import scala.collection.mutable.ListBuffer

object DemoKafka
{
  def main(args: Array[String]): Unit = {
  val topic = "kaf"
    println("*** about to start embedded Kafka server")

    val kafkaServer = new EmbeddedKafkaServer()
    kafkaServer.start()

    println("*** server started")

    kafkaServer.createTopic(topic, 4)

    println("*** topic [" + topic + "] created")

    Thread.sleep(5000)

    val kafkaClient = new GenericKafkaClient(kafkaServer)

    println("*** about to produce messages")

    val nu = 1 to 1000
    var kafkaKeyValues = new ListBuffer[KafkaData]()
    nu.foreach(n =>
      kafkaKeyValues += KafkaDataAsStringValue("key_" + n , "value_" +n)
    )
    kafkaClient.send(topic, kafkaKeyValues)

    println("*** produced messages")

    Thread.sleep(5000)

    println("*** about to consume messages")

    kafkaClient.consumeToConsole(
      topic,
      5)

    println("*** stopping embedded Kafka server")

    kafkaServer.stop()

    println("*** done")
  }
}
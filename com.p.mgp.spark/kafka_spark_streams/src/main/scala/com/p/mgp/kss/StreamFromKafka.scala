package com.p.mgp.kss

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory


object StreamFromKafka {

  val LOGGER   = LoggerFactory.getLogger(StreamFromKafka.getClass)

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("Read Stream From Kafka")
    sparkConf.setIfMissing("spark.master", "local[3]")

    // set the streaming Context

    val sparkStreamingContext  = new StreamingContext(sparkConf, Seconds(2))

    // define which kafka topic to read from
    val kafkaTopic = "ImpDATA"

    val kafkaBroker = "localhost:9092"

    val topics = Set[String] = kafkaTopic.split(",").map(_.trim).toSet

    val kafkaParams = Map[String, String] ("metadata.broker.list" -> kafkaBroker)

    LOGGER.info(s"Connecting to brokers: $kafkaBroker")
    LOGGER.info(s"Topics -> $topics")
    LOGGER.info(s"Params for Kafka -> $kafkaParams")

    // create a kafka stream
//    val impDataStream = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](sparkStreamingContext, kafkaParams, topics)

  }

}

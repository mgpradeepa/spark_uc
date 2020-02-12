package com.p.mgp.kss.serverutils

import scala.collection.JavaConversions._
import java.io.IOException
import com.typesafe.scalalogging.Logger
import kafka.admin.TopicCommand
import kafka.server.{KafkaConfig, KafkaServerStartable}
import kafka.utils.ZkUtils


/**
  * Take hold of the server setup
  * Create the necessary connections ,
  * Start and Stop the kafka server considering the underlying kafka server
  * Handling failure cases
  */

@throws[IOException]
class EmbeddedKafkaServer() {
  private val LOGGER  = Logger[EmbeddedKafkaServer]

  val tempDirs = new TemporaryDirectories
  val zkPort = 39001
  val kbPort = 39002
  val zkSessionTimeout = 20000
  val zkConnectionTimeout = 20000

  private var zookeeperHandle: Option[EmbeddedZookeeper] = None
  private var kafkaBrokerHandle: Option[KafkaServerStartable] = None

  // Start zookeeper and then start the kafka

  def start() :Unit  = {

    // for zookeeper
    LOGGER.info(s"Zookeeper starting on [$zkPort , $kbPort]")
    zookeeperHandle = Some(new EmbeddedZookeeper(zkPort, tempDirs))
    zookeeperHandle.get.start()

    // for kafka

    val kafkaProps =Map(
      "port" -> Integer.toString(kbPort),
      "broker.id" -> "1",
      "host.name" -> "localhost",
      "log.dir" -> tempDirs.kafkaLogDirPath,
      "zookeeper.connect" -> ("localhost:" + zkPort)

    )
    kafkaBrokerHandle = Some(new KafkaServerStartable(new KafkaConfig(kafkaProps)))
    kafkaBrokerHandle.get.startup()



  }

  /**
    * If running, shut down first the Kafka broker and then the Zookeeper
    */
  def stop() {
    LOGGER.info(s"shutting down broker on $kbPort")
    kafkaBrokerHandle match {
      case Some(b) => {
        b.shutdown()
        b.awaitShutdown()
        kafkaBrokerHandle = None
      }
      case None =>
    }
    Thread.sleep(5000)
    LOGGER.info(s"shutting down zookeeper on $zkPort")
    zookeeperHandle match {
      case Some(zk) => {
        zk.stop()
        zookeeperHandle = None
      }
      case None =>
    }
  }



  // function to create the topics in kafka
  def createTopic(topic:String, partitions:Int=1,logAppendTime:Boolean=false ) :Unit = {
    LOGGER.debug(s"Creating topic [ $topic ]")
    val arguments = Array[String] (
      "--create ",
      "--topic",
      topic
    ) ++ (
      if(logAppendTime) {
        Array[String]("--config", "message.timestamp.type=LogAppendTime")
      }else {
        Array[String]()
      }) ++ Array[String](
      "--partitions",
      "" + partitions,
      "--replication-factor",
      "1"
    )
    val opts = new TopicCommand.TopicCommandOptions(arguments)
    val zkUtils = ZkUtils.apply(getZkConnect,
      zkSessionTimeout, zkConnectionTimeout, isZkSecurityEnabled = true)

    TopicCommand.alterTopic(zkUtils, opts)

    LOGGER.debug(s"Finished adding [$partitions] partitions to [$topic]")

  }

  def getKafkaConnect: String = "localhost:" + kbPort

 def getZkConnect :String = "localhost" +zkPort
}

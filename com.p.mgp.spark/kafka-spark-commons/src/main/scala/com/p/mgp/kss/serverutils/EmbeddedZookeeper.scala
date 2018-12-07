package com.p.mgp.kss.serverutils

import java.io.IOException
import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import org.apache.zookeeper.server.{NIOServerCnxnFactory, ServerCnxnFactory, ZooKeeperServer}

case class EmbeddedZookeeper (port:Int, tempDirs : TemporaryDirectories){
  private val LOGGER = Logger[EmbeddedZookeeper]

  private var serverConnectionFactory : Option[ServerCnxnFactory] = None

  /** Start a single instance of zookeeper **/

  def start(): Unit = {

    LOGGER.info(s"Zookeeper starts at PORT -> $port")
    try{
      val zkMaxConnections = 32
      val zkTickTime = 2000

      // create connections
      val zkServer = new ZooKeeperServer(tempDirs.zkSnapshotDir, tempDirs.zkLogDir, zkTickTime)
      serverConnectionFactory = Some(new NIOServerCnxnFactory())
      serverConnectionFactory.get.configure(new InetSocketAddress("localhost", port), zkMaxConnections)
      serverConnectionFactory.get.startup(zkServer)

    }
    catch {
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
      case e: IOException =>
        throw new RuntimeException("Unable to start ZooKeeper", e)
    }
  }

  // stop the zookeeper instance
  def stop():Unit = {
    LOGGER.info(s"Stop the zookeepr conection")
    serverConnectionFactory match {
      case Some(f) => {
        f.shutdown()
        serverConnectionFactory = None
      }
      case None =>
    }
  }







}

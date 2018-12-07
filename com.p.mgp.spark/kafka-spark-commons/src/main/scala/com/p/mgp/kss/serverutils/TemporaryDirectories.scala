package com.p.mgp.kss.serverutils

import java.io.File

case class TemporaryDirectories (){
  val tempRootPath = java.io.File.separator + "tmp" + java.io.File.separator + "SSWK"

  val checkpointPath = tempRootPath + File.separator + "checkpoints"

  private val rootDir = new File(tempRootPath)

  deleteRecursively(rootDir)
  rootDir.mkdir

  val zkSnapshotPath: String = tempRootPath + File.separator + "zookeeper-snapshot"
  val zkSnapshotDir = new File(zkSnapshotPath)
  zkSnapshotDir.mkdir()

  val zkLogDirPath = tempRootPath + File.separator + "zookeeper-logs"
  val zkLogDir = new File(zkLogDirPath)
  zkLogDir.mkdir()

  val kafkaLogDirPath = tempRootPath + File.separator + "kafka-logs"
  val kafkaLogDir = new File(kafkaLogDirPath)
  kafkaLogDir.mkdir()

  Runtime.getRuntime.addShutdownHook(new Thread {
    try {
      deleteRecursively(rootDir)
    }
    catch {
      case e: Exception =>
    }
  })

  def deleteRecursively(context: File):Unit = {
    if(context.isDirectory)
      context.listFiles.foreach(deleteRecursively)
    if(context.exists && !context.delete)
      throw new Exception(s"Unable to delete ${context.getAbsolutePath}")
  }
}

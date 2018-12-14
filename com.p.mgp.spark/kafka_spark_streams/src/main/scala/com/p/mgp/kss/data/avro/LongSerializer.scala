package com.p.mgp.kss.data.avro

import java.nio.{ByteBuffer, LongBuffer}
import java.util

import org.apache.kafka.common.serialization.Serializer

class LongSerializer  extends Serializer[Long]{
  override def configure(map: util.Map[String, _], b: Boolean): Unit = ???

  override def serialize(stat: String, along: Long): Array[Byte] = longToByteArray(along)

  override def close(): Unit = ???


  def longToByteArray(l: Long) : Array[Byte] =
    ByteBuffer.allocate(8).putLong(l).array()
}

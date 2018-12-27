package com.p.mgp.kss.data.avro

import java.io.ByteArrayOutputStream
import java.util

import org.apache.kafka.common.serialization.Serializer
import org.apache.avro.generic.{GenericContainer, GenericDatumWriter}
import org.apache.avro.io.{BinaryEncoder, EncoderFactory}
import org.apache.kafka.common.errors.SerializationException


@throws[SerializationException]
class AvroGenericRecordSerializer extends Serializer[GenericContainer]  {

  override def serialize(topic: String, data: GenericContainer): Array[Byte] = {
    val writer = new GenericDatumWriter[GenericContainer](KSSAvroSchema.avroSchema)
    val out = new ByteArrayOutputStream
    val encoder : BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
    writer.write(data,encoder)
    encoder.flush()
    out.close()

    val serializedBytes:Array[Byte] = out.toByteArray

    serializedBytes

  }

  override def configure(map: util.Map[String, _], b: Boolean): Unit = ???

  override def close(): Unit = ???

}
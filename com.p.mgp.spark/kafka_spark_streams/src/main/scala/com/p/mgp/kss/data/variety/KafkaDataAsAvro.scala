package com.p.mgp.kss.data.variety

import org.apache.avro.generic.{GenericContainer, GenericRecord}

case class KafkaDataAsAvro(keys:Long, avroDataAsValue: GenericContainer) extends KafkaData {
  override val key: String = keys.toString
  override val value: Object = avroDataAsValue
}

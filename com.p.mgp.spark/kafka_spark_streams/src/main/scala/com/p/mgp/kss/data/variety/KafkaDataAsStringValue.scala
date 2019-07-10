package com.p.mgp.kss.data.variety

case class KafkaDataAsStringValue(keys:String, values:String) extends KafkaData {
  override val key: String = keys
  override val value: String = values
}

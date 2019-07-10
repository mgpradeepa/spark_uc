package com.p.mgp.kss.data.avro

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}

import scala.io.Source

object KSSAvroSchema {

  val avroSchema : Schema = new Parser().parse(Source.fromURL(getClass.getResource("/IMPDATA.avsc")).mkString)

  val genericRecord: GenericRecord = new GenericData.Record(avroSchema)

  // this is just a sample
  def populateSampleRecord : GenericRecord = {
    genericRecord.put("id",1L)
    genericRecord.put("datedOn", new java.util.Date())
    genericRecord.put("status", "SUCCESS")
    genericRecord.put("subStatus", "Everything was fine")
    genericRecord
  }




}

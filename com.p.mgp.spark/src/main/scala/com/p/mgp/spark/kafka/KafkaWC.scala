package com.p.mgp.spark.kafka


import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

/**
 * @author mgpradeepa
 */
object KafkaWC {
  def main(args:Array[String]){
    if(args.length < 4) {
      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>")
      System.exit(1)
    }
    val Array(zkQuorum, group, topics, numThreads) = args
    
    val sparkConf = new SparkConf().setAppName("KafkaWC")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")
    
    val topicMap = topics.split(",").map (( _, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    val words = lines.flatMap (_.split(" "))
    val wordCount = words.map(x=>(x,1L))
//    .reduceByKeyan
    
    
    
    
  }
  
}
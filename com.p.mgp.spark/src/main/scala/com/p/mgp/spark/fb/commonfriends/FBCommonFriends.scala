package com.p.mgp.spark.fb.commonfriends

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * @author mgpradeepa
 */
object FBCommonFriends extends App {
  val APP_NAME = "CommonFriends"
  val RUN_MODE = "local"

  val inputFile = "/Users/mgpradeepa/Public/mgp_gitter/spark_git/spark_uc/com.p.mgp.spark/src/main/scala/resources/ip/fbfriends/"
  println(s"input file  $inputFile")

  val outputFile = "/Users/mgpradeepa/Public/mgp_gitter/spark_git/spark_uc/com.p.mgp.spark/src/main/scala/resources/op/fbcommonfriends" //args(1)
  println(s"output file => $outputFile")

  val rdd = new SparkConf().setAppName(APP_NAME).setMaster(RUN_MODE)
  val sparkcontext = new SparkContext(rdd)
  val inp = sparkcontext.textFile(inputFile+"*")
  println(inp.collect())
  
  // read the text data
  val f = inp.flatMap { line => line.split("\\n") }
  println(f.collect())
//  val eachFriend = f.map { x => ??? } 
  

  

}
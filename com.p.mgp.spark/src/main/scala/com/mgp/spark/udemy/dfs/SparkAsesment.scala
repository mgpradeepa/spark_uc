package com.mgp.spark.udemy.dfs

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
/**
  * Created by mgpradeepa on 30/06/18.
  */
object SparkAsesment {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("Usecase1").getOrCreate()

    val df = spark.read.option("header","true").option("inferSchema","true").csv("../resources/Netflix.csv")

    df.columns
    df.show()

  }




}

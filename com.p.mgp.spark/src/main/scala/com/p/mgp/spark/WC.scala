package com.p.mgp.spark
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * @author mgpradeepa
 */
object WC {
  def main(args: Array[String]) {
    // args(0):/ home/ubuntu/input/words.count/Mgpspark-1.4-SQL-and-DataFrames.txt
    // args(1): /home/ubuntu /output/spark.save.file.output/<mgp6>
    ///
//    args(0) = "/Users/mgpradeepa/Documents/workspace/com.p.mgp.spark/src/main/scala/resources/dummy"
//    args(1) = "/Users/mgpradeepa/Documents/workspace/com.p.mgp.spark/src/main/scala/resources/op"

    val inputFile = "/Users/mgpradeepa/Public/mgp_gitter/spark_git/spark_uc/com.p.mgp.spark/src/main/scala/resources/ip"
    println(s"input file  $inputFile")

    val outputFile = "/Users/mgpradeepa/Public/mgp_gitter/spark_git/spark_uc/com.p.mgp.spark/src/main/scala/resources/op" //args(1)
    println(s"output file => $outputFile")

    val rdd = new SparkConf().setAppName("WC").setMaster("local")
    val sparkcontext = new SparkContext(rdd)
    // Load the input
    val inp = sparkcontext.textFile(inputFile)

    // split up into word

    val words = inp.flatMap { line => line.split(",") } //line.split("\\s+")
    // transform into word and count
    val counts = words.map(word => (word, 1)).reduceByKey { case (x, y) => x + y }
    val countsTransform = words.map(word => (word, 1))
    val countsAction = countsTransform.reduceByKey { case (x, y) => x + y }

    val cont = inp.filter(_.startsWith("v"))
    val dat = cont.map(_.split("\\s+")(0))
    //    dat.cache()
    println(dat.filter(_.contains("val")).count() + "val")
    println(dat.filter(_.contains("var")).count() + "var")

    counts.saveAsTextFile(outputFile)
    println(counts.count())

  }
}

/***
 * import org.apache.spark.SparkConf

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

/**
  * Created by pradeepm.gireesha on 3/4/2016.
  */
object WordCountExample {

  def main(args: Array[String]) {
    // args(0):/ home/ubuntu/input/words.count/Mgpspark-1.4-SQL-and-DataFrames.txt
    // args(1): /home/ubuntu /output/spark.save.file.output/<mgp6>
    ///

    val inputFile = args(0)
    println("input file " + inputFile)

    val outputFile = args(1)
    println(s"output file => $outputFile")

    val rdd = new SparkConf().setAppName("WordCountExample").setMaster("local")
    val sparkcontext = new SparkContext(rdd)
    // Load the input
    val inp = sparkcontext.textFile(inputFile)

    // split up into word

    val words = inp.flatMap { line => line.split("\\s+") }
    // transform into word and count
    val counts = words.map(word => (word, 1)).reduceByKey { case (x, y) => x + y }

    val cont = inp.filter(_.startsWith("v"))
    val dat = cont.map(_.split("\\s+")(0))
//    dat.cache()
    println(dat.filter(_.contains("val")).count() + "val")
    println(dat.filter(_.contains("var")).count() + "var")

    counts.saveAsTextFile(outputFile)
    println(counts.count())

  }


}
 */



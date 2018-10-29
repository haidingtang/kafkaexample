package com.example

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkConf


object SparkKafkaOffset2Consumer {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("test").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))

    val servers = "server01:9092"
    val groupId = "test.offset.group.id"
    val topics = Array("test")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> servers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val offsetRanges = Array(
      OffsetRange("test", 0, 8L, 10L),
      OffsetRange("test", 1, 8L, 10L)
    )

    //    val rdd = KafkaUtils.createRDD[String, String](ssc.sparkContext, kafkaParams, offsetRanges, LocationStrategies.PreferConsistent)
    //        rdd.foreachRDD(results => {
    //          val offsetsList = results.asInstanceOf[HasOffsetRanges].offsetRanges
    //          results.foreachPartition { partitionOfRecords =>
    //            partitionOfRecords.foreach(record => {
    //              //          System.out.println(record.value())
    //            })
    //          }
    //        })

    ssc.start()
    ssc.awaitTermination()
  }

}

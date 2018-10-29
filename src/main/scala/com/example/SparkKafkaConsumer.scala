package com.example

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * Created by zhang on 2018/7/14.
  */
object SparkKafkaConsumer {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("test").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))

    val servers = "server01:9092"
    val groupId = "test.group.id"
    val topics = Array("test")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> servers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val rdd = KafkaUtils.createDirectStream(ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))

    rdd.foreachRDD(results => {
      results.foreachPartition { partitionOfRecords =>
        partitionOfRecords.foreach(record => {
          System.out.println(record.value())
        })
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}

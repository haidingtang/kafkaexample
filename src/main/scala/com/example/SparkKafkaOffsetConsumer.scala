package com.example

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.{SparkConf, TaskContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * Created by zhang on 2018/7/14.
  */
object SparkKafkaOffsetConsumer {


  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache").setLevel(Level.ERROR)

    val conf = new SparkConf().setAppName("test").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))

    val servers = "dev-bg-01:9092"
    val groupId = "test.offset.group.id"
    val topics = Array("sit_db_nono_b")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> servers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val stream = KafkaUtils.createDirectStream(ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )
    //    getOffsets1(stream)
    //    getOffsets2(stream)
    commitOffsets(stream)
    ssc.start()
    ssc.awaitTermination()
  }


  /**
    * 从指定的分区提交
    */
  def getOffsets1(stream: InputDStream[ConsumerRecord[String, String]]): Unit = {
    stream.foreachRDD { rdd =>
      rdd.foreachPartition { records =>
        records.foreach(record => {
          val offset = record.offset()
          println(s"${record.topic} ${record.partition} ${record.offset()}")
          // 可以根据消费的
          if (record.partition() == 0 && offset > 36) {
            System.out.println(record.value)
          } else if (record.partition() == 1 && offset > 37) {
            System.out.println(record.value)
          } else if (record.partition() == 2 && offset > 37) {
            System.out.println(record.value)
          }
        }
        )
      }
    }
  }

  /*
   * 从指定的分区提交2
   */
  def getOffsets2(stream: InputDStream[ConsumerRecord[String, String]]): Unit = {
    stream.foreachRDD { rdd =>
      // 获取偏移量
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd.foreachPartition {
        val offsetRange: OffsetRange = offsetRanges(TaskContext.getPartitionId())
        println(s"${offsetRange.topic} ${offsetRange.partition} ${offsetRange.fromOffset} ${offsetRange.untilOffset}")
        records => {
          if ((offsetRange.partition == 0 && offsetRange.fromOffset > 43)
            || (offsetRange.partition == 1 && offsetRange.fromOffset > 41)
            || (offsetRange.partition == 2 && offsetRange.fromOffset > 42)) {
            records.foreach(record =>
              System.out.println(record.value())
            )
          }
        }
      }
    }
  }

  /**
    * 提交offset
    */
  def commitOffsets(stream: InputDStream[ConsumerRecord[String, String]]): Unit = {
    stream.foreachRDD { rdd =>
      // 获取offset
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      // 业务逻辑处理
      rdd.foreachPartition {
        records => {
          records.foreach { record => {
            System.out.println(record.topic() + "   " + record.partition() + "    " + record.offset() + "   " + record.value())
          }
          }
        }
      }
      // kafka 提交偏移量
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      System.out.println(offsetRanges.toList)
    }
  }
}

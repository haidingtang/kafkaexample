package com.example;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Created by zhang on 2018/7/15.
 */
public class KafkaProducerDemo {

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "server01:9092");
        properties.put("client.id", "KafkaProducerDemo");
        properties.put("request.required.acks", "1");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        String topic = "test";
        int partition = 1;
        String key = "";
        boolean async = true;
        boolean isUniquePartition = true;

        if (StringUtils.isEmpty(topic)) return;
        ProducerRecord<String, String> record;
        while (true) {
            long currentTime = System.currentTimeMillis();
            String value = UUID.randomUUID().toString() + "_" + currentTime;
            if (isUniquePartition) {
                // 指定分区
                record = new ProducerRecord<>(topic, partition, currentTime, key, value);
            } else {
                record = new ProducerRecord<>(topic, value);
            }
            if (!async) {
                // 同步发送
                Future<RecordMetadata> future = producer.send(record);
                future.get();
            } else {
                // 异步发送
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        System.out.println(recordMetadata.partition() + "    " + recordMetadata.offset());
                    }
                });
            }
            System.out.println("-------message-----------: " + value);
            Thread.sleep(1000);
        }
    }
}

package com.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;


public class KafkaConsumerDemo {
    public static void main(String[] args) throws InterruptedException {

        Properties props = new Properties();
        props.put("bootstrap.server", "server01:2181");
        props.put("group.id", "test.group.id");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "1");
        props.put("value.deserializer", "2");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singletonList("test"));
        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(5000);
            for (ConsumerRecord record : consumerRecords) {
                System.out.println(record.topic() + " " + record.partition() + "  " + record.key() + " " + record.value());
            }
            Thread.sleep(1000);
        }

    }
}

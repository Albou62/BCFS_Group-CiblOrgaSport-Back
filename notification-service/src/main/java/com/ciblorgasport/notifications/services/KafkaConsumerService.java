package com.ciblorgasport.notifications.services;
import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

public class KafkaConsumerService {
    private KafkaConsumer<String, String> consumer;

    public KafkaConsumerService(String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "tcp-service-" + System.currentTimeMillis());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        props.put("enable.auto.commit", "true");

        startListening(topic);
    }
    
    private void startListening(String topic) {
        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    if (!records.isEmpty()) {
                        for (ConsumerRecord<String, String> record : records) {
                            JSONObject json = new JSONObject(record.value());
                        }
                    } else {
                        consumer.close();
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur KafkaListener " + topic + ": " + e.getMessage());
            }
        });
        
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
}

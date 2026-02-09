package com.ciblorgasport.notifications.services;

import java.time.Duration;
import java.util.Properties;
import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONArray;
import org.json.JSONObject;

public class KafkaConsumerService {
    private KafkaConsumer<String, String> consumer;
    private JSONArray notifs = new JSONArray();
    private volatile boolean running = true;

    public KafkaConsumerService(String topic) {
        Properties props = new Properties();
        // Use same bootstrap server as producer
        props.put("bootstrap.servers", "kafka:9092"); 
        // Fixed group ID
        props.put("group.id", "tcp-service-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "latest");
        props.put("enable.auto.commit", "true");
        
        this.consumer = new KafkaConsumer<>(props);
        startListening(topic);
    }
    
    private void startListening(String topic) {
        Thread listenerThread = new Thread(() -> {
            try {
                // Subscribe to topic
                consumer.subscribe(Collections.singletonList(topic));
                
                while (running) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    
                    if (!records.isEmpty()) {
                        for (ConsumerRecord<String, String> record : records) {
                            System.out.println("Received message: " + record.value());
                            JSONObject json = new JSONObject(record.value());
                            synchronized(notifs) {
                                this.notifs.put(json);
                            }
                        }
                    }
                    // Don't close consumer on empty poll!
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                consumer.close();
            }
        });
        
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public JSONArray getNotifs() { 
        synchronized(notifs) {
            return new JSONArray(this.notifs.toString());
        }
    }
    
    public void stop() {
        running = false;
    }
}
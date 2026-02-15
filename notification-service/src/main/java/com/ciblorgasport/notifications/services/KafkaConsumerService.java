package com.ciblorgasport.notifications.services;

import java.time.Duration;
import java.util.Properties;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

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
    private CountDownLatch messagesReceivedLatch = new CountDownLatch(1);

    public KafkaConsumerService(String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("group.id", "tcp-service-group-" + System.currentTimeMillis());
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "true");
        props.put("max.poll.records", "100");
        
        this.consumer = new KafkaConsumer<>(props);
        startListening(topic);
    }
    
    private void startListening(String topic) {
        Thread listenerThread = new Thread(() -> {
            try {
                consumer.subscribe(Collections.singletonList(topic));
                System.out.println("Subscribed to topic: " + topic);
                
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
                        // Signal that we've received messages
                        messagesReceivedLatch.countDown();
                    }
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

    public JSONArray getNotifs(long timeoutMillis) { 
        try {
            // Wait for messages to be received
            messagesReceivedLatch.await(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        synchronized(notifs) {
            return new JSONArray(this.notifs.toString());
        }
    }
    
    public void stop() {
        running = false;
    }
}
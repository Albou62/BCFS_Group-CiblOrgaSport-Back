package com.ciblorgasport.notifications.services;

import java.time.Duration;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONArray;

public class KafkaConsumerService {
    private KafkaConsumer<String, String> consumer;
    private List<String> messages = new ArrayList<>();
    private volatile boolean running = true;
    private CountDownLatch messagesReceivedLatch = new CountDownLatch(1);
    Properties props;

    public KafkaConsumerService(String topic) {
        this.props = new Properties();
        String bootstrapServers = "kafka:9093";
        this.props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        System.out.println("Connecting to Kafka at: " + bootstrapServers);
        this.props.put(ConsumerConfig.GROUP_ID_CONFIG, "tcp-service-group-" + System.currentTimeMillis());
        this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        this.props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        this.props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        
        try {
            this.consumer = new KafkaConsumer<>(this.props);
            System.out.println("Kafka producer created successfully");
            startListening(topic);
        } catch (Exception e) {
            System.err.println("Failed to create Kafka producer: " + e.getMessage());
            e.printStackTrace();
        }
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
                            synchronized(messages) {
                                this.messages.add(record.value());  // Store as string
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

    public JSONArray getMessagesAsJson(long timeoutMillis) { 
        try {
            // Wait for messages to be received
            messagesReceivedLatch.await(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        synchronized(messages) {
            // Convert strings to JSONArray
            JSONArray jsonArray = new JSONArray();
            for (String msg : messages) {
                jsonArray.put(msg);  // This will create a JSON array of strings
            }
            return jsonArray;
        }
    }
    
    public List<String> getMessages(long timeoutMillis) {
        try {
            messagesReceivedLatch.await(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        synchronized(messages) {
            return new ArrayList<>(messages);  // Return a copy
        }
    }
    
    public void stop() {
        running = false;
    }
}
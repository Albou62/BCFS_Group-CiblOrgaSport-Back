package com.ciblorgasport.notifications.services;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerService {
    private KafkaProducer<String, String> producer;
    private Properties props;

    public KafkaProducerService() {
        this.props = new Properties();
        // kafka:9093 pour communication sur Docker
        String bootstrapServers = "kafka:9093"; // Try both 9092 and 9093
        this.props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        System.out.println("Connecting to Kafka at: " + bootstrapServers);
        this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.props.put(ProducerConfig.ACKS_CONFIG, "1");
        this.props.put("allow.auto.create.topics", "true");
        
        try {
            this.producer = new KafkaProducer<>(this.props);
            System.out.println("Kafka producer created successfully");
        } catch (Exception e) {
            System.err.println("Failed to create Kafka producer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String userTopic, long groupId, String label) {
        try {
            String key = String.valueOf(groupId);
            System.out.println("Attempting to send to topic: " + userTopic);
            System.out.println("Bootstrap servers: " + this.props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
            
            ProducerRecord<String, String> record = 
                new ProducerRecord<>(userTopic, key, label);
            
            // Synchronous send for debugging
            var future = producer.send(record);
            var metadata = future.get(); // This will block and throw if there's an error
            
            System.out.println("Success! Sent to topic " + metadata.topic() + 
                " partition " + metadata.partition() + 
                " at offset " + metadata.offset());
                
        } catch (Exception e) {
            System.err.println("Send failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

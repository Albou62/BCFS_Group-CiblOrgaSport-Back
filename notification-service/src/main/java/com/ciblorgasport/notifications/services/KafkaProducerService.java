package com.ciblorgasport.notifications.services;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerService {
    private KafkaProducer<String, String> producer;

    public KafkaProducerService() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        props.put("allow.auto.create.topics", "true");
        
        this.producer = new KafkaProducer<>(props);
    }

    public void sendMessage(String userTopic, long groupId, String label) {
        try {
            String key = String.valueOf(groupId);
            System.out.println(""+userTopic+" "+groupId+" "+label);
            
            ProducerRecord<String, String> record = 
                new ProducerRecord<>(userTopic, key, label);
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Send failed: " + exception.getMessage());
                } else {
                    System.out.println("Sent to partition " + metadata.partition());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

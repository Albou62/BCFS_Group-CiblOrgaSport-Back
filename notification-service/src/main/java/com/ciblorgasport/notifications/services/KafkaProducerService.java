package com.ciblorgasport.notifications.services;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerService {
    private KafkaProducer<String, String> producer;

    public KafkaProducerService() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        
        this.producer = new KafkaProducer<>(props);
    }

    public void sendMessage(long userTopic, long groupId, String label) {
        try {
            String topicName = String.valueOf(userTopic);
            String key = String.valueOf(groupId);
            
            ProducerRecord<String, String> record = 
                new ProducerRecord<>(topicName, key, label);
            
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
            System.out.println("Partition: " + metadata.partition() + ",Offset: " + metadata.offset());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

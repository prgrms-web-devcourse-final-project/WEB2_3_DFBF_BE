package org.dfbf.soundlink.global.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.apache.kafka.common.serialization.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;

    // =============================== [producer] ===============================
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        // Kafka producer 설정을 위한 Properties 객체 생성
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Kafka broker의 주소와 포트 설정
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // key serializer 설정
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // value serializer 설정

        // Properties 객체를 사용하여 ProducerFactory 생성
        return new DefaultKafkaProducerFactory<>(properties);
    }

    // KafkaTemplate을 생성하기 위한 Bean 메서드
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        // ProducerFactory를 사용하여 KafkaTemplate 생성
        return new KafkaTemplate<>(producerFactory());
    }

    // =============================== [Consumer 설정] ===============================
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        // Kafka consumer 설정을 위한 Properties 객체 생성
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);                // Kafka broker의 주소와 포트 설정
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, applicationId);                         // consumer group ID 설정
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);   // key deserializer 설정
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // value deserializer 설정

        // Properties 객체를 사용하여 ConsumerFactory 생성
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    // Kafka listener container를 생성하기 위한 Bean 메서드
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        // ConcurrentKafkaListenerContainerFactory 생성
        ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        // ConsumerFactory를 사용하여 Kafka listener container를 생성함
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());

        return kafkaListenerContainerFactory;
    }
}
package org.dfbf.soundlink.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.streams.application-id}")
//    private String applicationId;
//
//    @Bean
//    public KafkaStreamsDefaultConfiguration kStreamsConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
//
//        return new KafkaStreamsDefaultConfiguration();
//    }
//
//    @Bean
//    public KStream<String, String> simpleStream(StreamsBuilder builder) {
//        // "chat-topic" 토픽 구독 (테스트용)
//        KStream<String, String> stream = builder.stream("alert-topic");
//        stream.foreach((key, value) -> System.out.println("Received message: " + key + " -> " + value));
//        return stream;
//    }
}
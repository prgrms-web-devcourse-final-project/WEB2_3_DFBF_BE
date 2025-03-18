package org.example.soundlinkchat_java.domain.chat.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;

    @Bean
    public KafkaStreamsDefaultConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        return new KafkaStreamsDefaultConfiguration();
    }

    @Bean
    public KStream<String, String> notificationStream(StreamsBuilder builder) {

        return builder.stream(
                "chat-topic",
                Consumed.with(Serdes.String(), Serdes.String())
        );
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilderFactoryBean factoryBean) throws Exception {

        KStream<String, String> sourceStream = factoryBean.getObject().stream(
                "chat-topic",
                Consumed.with(Serdes.String(), Serdes.String())
        );

        KStream<String, String> transformedStream = sourceStream
                .mapValues(value -> "알람으로 보낼 value : " + value);

        transformedStream.to(
                "chat-topic-processed",
                Produced.with(Serdes.String(), Serdes.String())
        );
        return sourceStream;
    }
}

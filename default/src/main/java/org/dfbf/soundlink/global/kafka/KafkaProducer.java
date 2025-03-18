package org.dfbf.soundlink.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.alert.entity.Alert;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    // 주어진 주제(topic)로 OrderDto 객체를 Kafka 메시지로 전송하고, 전송한 OrderDto 객체를 반환하는 메서드
    public Alert send(String topic, Alert alert) {
        String jsonInstring = "";

        try {
            jsonInstring = mapper.writeValueAsString(alert); // OrderDto 객체를 JSON 문자열로 변환
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
        }

        kafkaTemplate.send(topic, jsonInstring); // KafkaTemplate을 사용하여 Kafka 메시지를 보냄

        return alert; // 전송한 OrderDto 객체를 반환
    }
}

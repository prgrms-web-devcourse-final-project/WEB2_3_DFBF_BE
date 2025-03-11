package org.dfbf.soundlink.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.alert.entity.Alert;
import org.dfbf.soundlink.domain.alert.repository.AlertRepository;
import org.dfbf.soundlink.domain.alert.service.AlertService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AlertService alertService;
    private final AlertRepository alertRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    // KafkaListener annotation을 통해 메시지를 수신함
    @KafkaListener(topics = "alert-topic", groupId = "alert-service")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message: -> {}", kafkaMessage);

        try {
            Alert alert = mapper.readValue(kafkaMessage, Alert.class);

            if (alertRepository.getEmitterId(alert.getUserId()).isPresent()) {
                log.info("알림 전송: {}", alert.getUserId());
                alertService.send(alert.getUserId(), alert.getType(), alert.getData());
            } else {
                //redisTemplate.opsForValue().set("alert:" + alert.getEventId(), alert, 10, TimeUnit.MINUTES);
                log.info("No SSE connection for user {}.", alert.getUserId());
            }
        } catch (JsonProcessingException e) {
            log.error("JSON parsing error: {}", e.getMessage());
        }
    }
}

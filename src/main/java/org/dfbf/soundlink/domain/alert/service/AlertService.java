package org.dfbf.soundlink.domain.alert.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.alert.entity.Alert;
import org.dfbf.soundlink.domain.alert.repository.AlertRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 60 * 1000 * 60 = 3,600,000{ms} = 1시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private String createEmitterId(Long userId) {
        return String.valueOf(userId) + "_" + System.currentTimeMillis();
    }

    // 사용자에게 전송되지 않은 알림을 Redis에서 꺼내서 전송하고 삭제
    private void sendPendingAlerts(Long userId, SseEmitter sseEmitter) {
        String keyPattern = "alert:" + userId + "_*";  // 사용자 알림에 대한 키 패턴

        // 해당 키 패턴을 가진 모든 알림을 가져오기
        redisTemplate.keys(keyPattern).forEach(key -> {
            Alert alert = (Alert) redisTemplate.opsForValue().get(key); // Redis에서 알림 가져오기
            if (alert != null) {
                try {
                    log.info("Sending pending alert to user {}", userId);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonMsg = objectMapper.writeValueAsString(alert.getData());

                    /* 알림 전송 -> 요청으로 비활성화
                    sseEmitter.send(SseEmitter.event()
                            .id(alert.getEventId())
                            .name(alert.getType())
                            .data(jsonMsg)
                    ); */

                    // 알림을 Redis에서 삭제
                    redisTemplate.delete(key);
                    log.info("Pending alert sent and removed from Redis for user {}", userId);
                } catch (IOException e) {
                    log.error("Error sending pending alert", e);
                }
            }
        });
    }

    // SSE 서버 연결
    public SseEmitter connectAlarm(Long id, String lastEventId) {

        if (lastEventId != null && !lastEventId.isEmpty()) {
            log.info("[lastEventId] {}", lastEventId);
        }

        // emitterId 생성 & 저장
        String emitterId = this.createEmitterId(id);
        alertRepository.saveEmitterId(id, emitterId);

        // SseEmitter 생성
        SseEmitter sseEmitter = alertRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        sseEmitter.onCompletion(() -> {
            log.error("[연결종료 로그] {}", emitterId);
            alertRepository.delete(id, emitterId);
        });  // 연결 종료 시 처리
        sseEmitter.onTimeout(() -> {
            log.error("[타임아웃 로그] {}", emitterId);
            alertRepository.delete(id, emitterId);
        });  // 타임아웃 시 처리

        try {
            log.info("아아 알림 테스트 {}", emitterId);
            sseEmitter.send(SseEmitter.event()
                    .id(this.createEmitterId(id))
                    .name("open")
                    .data("connect completed!!")
            );
        } catch (IOException e) {
            log.error("Error sending ping", e);
        }

        // 사용자에게 전송되지 않은 알림 전송
        this.sendPendingAlerts(id, sseEmitter);

        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                Thread.sleep(40000); // 45초마다 빈 메시지를 전송
                sseEmitter.send(SseEmitter.event().name("ping").data("connection keep-alive"));
            }
        });

        return sseEmitter;
    }

    // SSE를 통해 메시지 전송
    public void send(Long userId, String alertName, Object data) {
        String eventId = this.createEmitterId(userId);

        String emitterId = alertRepository.getEmitterId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found EmitterId: " + userId));

        SseEmitter sseEmitter = alertRepository.get(emitterId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found SseEmitter: " + userId));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMsg = objectMapper.writeValueAsString(data); // msg를 JSON 문자열로 변환

            log.info("아아 알림 테스트 {}", emitterId);
            sseEmitter.send(
                    SseEmitter.event()
                            .id(eventId)
                            .name(alertName)
                            .data(jsonMsg, MediaType.APPLICATION_JSON) // 변환된 JSON 문자열 전송
            );
        } catch (IOException e) {
            alertRepository.delete(userId, emitterId);
        }
    }

    // 사용자 SSE 연결 해제
    public void disconnectAlarm(Long userId) {
        String emitterId = alertRepository.getEmitterId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found EmitterId: " + userId));

        SseEmitter sseEmitter = alertRepository.get(emitterId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found SseEmitter: " + userId));

        alertRepository.delete(userId, emitterId);
        sseEmitter.complete();
    }

    // Alert 객체 생성
    public Alert createAlert(Long userId, String type, Object data) {
        return Alert.builder()
                .eventId(this.createEmitterId(userId))
                .type(type)
                .userId(userId)
                .data(data)
                .build();

    }
}

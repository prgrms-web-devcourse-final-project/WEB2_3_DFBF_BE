package org.dfbf.soundlink.domain.alert.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.alert.repository.AlertRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
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

    // 60 * 1000 * 60 = 3,600,000{ms} = 1시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final String USER_PREFIX = "user:";

    private String createEmitterId(Long userId) {
        return String.valueOf(userId) + "_" + System.currentTimeMillis();
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

        sseEmitter.onCompletion(() -> alertRepository.delete(id, emitterId));  // 연결 종료 시 처리
        sseEmitter.onTimeout(() -> alertRepository.delete(id, emitterId));     // 타임아웃 시 처리

        try {
            sseEmitter.send(SseEmitter.event()
                    .id(this.createEmitterId(id))
                    .name("open")
                    .data("connect completed!!")
            );
        } catch (IOException e) {
            log.error("Error sending ping", e);
        }

        // 하트비트 전송을 위한 스케줄러 설정
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 연결 유지를 위한 빈 메시지 전송
                sseEmitter.send(SseEmitter.event()
                        .id(this.createEmitterId(id)) // 이벤트 아이디 설정
                        .name("ping") // 이름을 'ping'으로 설정
                        .data("connection keep-alive") // 데이터는 "connection keep-alive"
                );
            } catch (IOException e) {
                log.error("Error sending ping", e);
            }
        }, 0, 41, TimeUnit.SECONDS); // 41초마다 빈 메시지 전송 (기존 45초에서 변경)

        return sseEmitter;
    }

    // SSE를 통해 메시지 전송
    public ResponseResult send(Long userId, String alertName, Object data) {
        String eventId = alertName.equals("ping") ? "-1" : this.createEmitterId(userId);

        String emitterId = alertRepository.getEmitterId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found EmitterId: " + userId));

        SseEmitter sseEmitter = alertRepository.get(emitterId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found SseEmitter: " + userId));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMsg = objectMapper.writeValueAsString(data); // msg를 JSON 문자열로 변환

            sseEmitter.send(
                    SseEmitter.event()
                            .id(eventId)
                            .name(alertName)
                            .data(jsonMsg, MediaType.APPLICATION_JSON) // 변환된 JSON 문자열 전송
            );

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (IOException e) {
            alertRepository.delete(userId, emitterId);
            return new ResponseResult(ErrorCode.BAD_REQUEST_STATUS, e.getMessage());
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

    // 사용자가 온라인인지 오프라인인지 확인 (Use UserId)
    public boolean isOnline(Long userId) {
        return alertRepository.getEmitterId(userId).isPresent();
    }
}
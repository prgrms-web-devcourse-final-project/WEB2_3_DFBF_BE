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

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository emitterRepository;

    // 60 * 1000 * 60 = 3,600,000{ms} = 1시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final String ALARM_NAME = "alarm";
    private static final String USER_PREFIX = "user:";

    private String createAlarmId(Long userId, Long alarmId) {
        String username = String.valueOf(userId);
        if (alarmId == null) {
            return username + "_" + System.currentTimeMillis();
        }
        log.info("[SseEmitter] Create alarmId {}", alarmId);

        return username + "_" + alarmId;
    }

    // SSE 서버 연결
    public SseEmitter connectAlarm(Long id) {

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id, sseEmitter);

        // 종료 되었을 때 처리
        sseEmitter.onCompletion(() -> {
            log.info("[SseEmitter] {} SseEmitter Completed", USER_PREFIX + id);
            emitterRepository.delete(id);
        });

        // timeOut 시 처리
        sseEmitter.onTimeout(() -> {
            log.info("[SseEmitter] {} SseEmitter Timeout",  USER_PREFIX + id);
            emitterRepository.delete(id);
        });


//        // 연결 시 기존에 저장된 알람을 전송
//        sendSavedAlerts(id, sseEmitter);

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                sseEmitter.send(SseEmitter.event()
                        .id(createAlarmId(id, null))
                        .name("open")
                        .data("connect completed!!")
                );

                // 45초마다 빈 데이터를 보내어 연결을 유지
                while (true) {
                    Thread.sleep(40000); // 45초마다 빈 메시지를 전송
                    sseEmitter.send(SseEmitter.event().name("ping").data("connection keep-alive"));
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(e.getMessage());
            } finally {
                sseEmitter.complete();
            }
        });

        return sseEmitter;
    }

    // SSE를 통해 메시지 전송
    public ResponseResult send(/*Long alarmId,*/ Long userId, String alertName, Object msg) {
        SseEmitter sseEmitter = emitterRepository.get(userId)
                .orElseGet(() -> {
                    log.info("[SseEmitter] {} SseEmitter Not Founded", USER_PREFIX + userId);
                    return new SseEmitter(); // 기본 객체 반환 (예시)
                });

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMsg = objectMapper.writeValueAsString(msg); // msg를 JSON 문자열로 변환

            sseEmitter.send(
                    SseEmitter.event()
                            .id(createAlarmId(userId, null))
                            .name(alertName)
                            .data(jsonMsg, MediaType.APPLICATION_JSON) // 변환된 JSON 문자열 전송
            );

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (IOException e) {
            emitterRepository.delete(userId);
            log.error(e.getMessage(), e);
            return new ResponseResult(ErrorCode.BAD_REQUEST_STATUS, e.getMessage());
        }
    }

    // 사용자 SSE 연결 해제
    public void disconnectAlarm(Long userId) {
        SseEmitter sseEmitter = emitterRepository.get(userId)
                .orElseGet(() -> {
                    log.info("[SseEmitter] {} SseEmitter Not Founded",  USER_PREFIX + userId);
                    return new SseEmitter(); // 기본 객체 반환 (예시)
                });

        sseEmitter.complete();
    }
}

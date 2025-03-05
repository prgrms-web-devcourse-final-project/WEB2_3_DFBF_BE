package org.dfbf.soundlink.domain.alert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.alert.repository.AlertRepository;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.management.Notification;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository emitterRepository;

    // 60 * 1000 * 60 = 3,600,000{ms} = 1시간
    private final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final String ALARM_NAME = "alarm";

    private String createAlarmId(String username, Long alarmId) {
        if (alarmId == null) {
            return username + "_" + System.currentTimeMillis();
        }

        return username + "_" + alarmId;
    }

    // SSE 서버 연결
    public SseEmitter connectAlarm(String username) {
        // Member findMember = memberCacheRepository.getMember(username);

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(username, sseEmitter);

        // 종료 되었을 때 처리
        sseEmitter.onCompletion(() -> {
            emitterRepository.delete(username);
        });

        // timeOut 시 처리
        sseEmitter.onTimeout(() -> {
            emitterRepository.delete(username);
        });

        try {
            sseEmitter.send(SseEmitter.event().id(createAlarmId(username, null)).name(ALARM_NAME).data("connect completed!!"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            //throw new SnsApplicationException(ResponseCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

    // SSE를 통해 메시지 전송
    public void send(long alarmId, String username, String msg) {
        SseEmitter sseEmitter = emitterRepository.get(username)
                .orElseGet(() -> {
                    log.info("[SseEmitter] {} SseEmitter Not Founded", username);
                    return new SseEmitter(); // 기본 객체 반환 (예시)
                });

        try {
            sseEmitter.send(
                    SseEmitter.event()
                            .id(createAlarmId(username, alarmId))
                            .name(ALARM_NAME)
                            .data(msg)
            );
        } catch (IOException e) {
            emitterRepository.delete(username);
            // throw new SnsApplicationException(HttpStatus.INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }
    }

    // 사용자 SSE 연결 해제
    public void disconnectAlarm(String username) {
        SseEmitter sseEmitter = emitterRepository.get(username)
                .orElseGet(() -> {
                    log.info("[SseEmitter] {} SseEmitter Not Founded", username);
                    return new SseEmitter(); // 기본 객체 반환 (예시)
                });

        sseEmitter.complete();
    }
}

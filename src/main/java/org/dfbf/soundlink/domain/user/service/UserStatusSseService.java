package org.dfbf.soundlink.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.user.dto.response.UserStatusDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserStatusSseService {
    // 구독자들을 저장하기 위해 userId -> (emitterId -> SseEmitter) 맵 사용
    private final Map<Long, Map<String, SseEmitter>> sseEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        // 만료 시간: 60분
        SseEmitter emitter = new SseEmitter(60L * 60 * 1000);

        String emitterId = System.currentTimeMillis() + "-" + userId;

        sseEmitters.putIfAbsent(userId, new ConcurrentHashMap<>());
        sseEmitters.get(userId).put(emitterId, emitter);

        // 연결 완료 시 첫 이벤트 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connection established for userId=" + userId));
        } catch (IOException e) {
            log.error("SSE 구독 INIT 오류: {}", e.getMessage());
        }

        // 만료 또는 에러 시, 자동 제거
        emitter.onCompletion(() -> {
            log.info("[SSE] onCompletion callback called. userId={}, emitterId={}", userId, emitterId);
            removeEmitter(userId, emitterId);
        });
        emitter.onTimeout(() -> {
            log.info("[SSE] onTimeout callback called. userId={}, emitterId={}", userId, emitterId);
            removeEmitter(userId, emitterId);
        });
        emitter.onError((e) -> {
            log.info("[SSE] onError callback called. userId={}, emitterId={}, error={}", userId, emitterId, e.getMessage());
            removeEmitter(userId, emitterId);
        });

        return emitter;
    }

    private void removeEmitter(Long userId, String emitterId) {
        Map<String, SseEmitter> emitterMap = sseEmitters.get(userId);
        if (emitterMap != null) {
            emitterMap.remove(emitterId);
            if (emitterMap.isEmpty()) {
                sseEmitters.remove(userId);
            }
        }
    }

    // 유저상태 변경 알림
    public void sendUserStatus(Long userId, UserStatusDto status) {
        // userId의 SSE 구독자 목록을 찾아 이벤트를 보냄
        if (!sseEmitters.containsKey(userId)) {
            // 해당 userId를 구독 중인 클라이언트가 없으면 그냥 무시
            return;
        }
        Map<String, SseEmitter> emitterMap = sseEmitters.get(userId);

        emitterMap.forEach((emitterId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("STATUS")  // 이벤트 타입
                        .data(status));
            } catch (IOException e) {
                // 전송 실패 시 emitter 제거
                log.error("[SSE] 전송 실패: emitterId={} / userId={}, error={}", emitterId, userId, e.getMessage());
                removeEmitter(userId, emitterId);
            }
        });
    }
}

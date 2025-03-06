package org.dfbf.soundlink.domain.alert.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class AlertRepository {
    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    private String createKey(Long userId) {
        return "userAlarm:" + userId;
    }

    public SseEmitter save(Long userId, SseEmitter sseEmitter) {
        String key = this.createKey(userId);
        if (emitterMap.containsKey(key)) {
            emitterMap.remove(key);
        }
        emitterMap.put(key, sseEmitter);

        log.info("[SseEmitter] Set {}", key);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(Long userId) {
        String key = this.createKey(userId);
        SseEmitter sseEmitter = emitterMap.get(key);

        log.info("[SseEmitter] Get {}", key);
        return Optional.ofNullable(sseEmitter);
    }

    public void delete(Long userId) {
        emitterMap.remove(this.createKey(userId));
    }

    // 저장된 알람을 가져오는 메서드
//    public List<String> getSavedAlerts(Long userId) {
//        // 저장된 알람 목록을 리턴하는 로직을 추가
//        // 예시로 간단히 List<String> 타입으로, 필요에 따라 알람 객체를 리턴할 수도 있음
//        return new ArrayList<>(); // 이곳을 실제 알람 저장 로직으로 수정
//    }
}

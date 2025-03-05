package org.dfbf.soundlink.domain.alert.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class AlertRepository {
    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(String username, SseEmitter sseEmitter) {
        if (emitterMap.containsKey(username)) {
            emitterMap.remove(username);
        }
        emitterMap.put(username, sseEmitter);

        log.info("[SseEmitter] Set {}", username);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(String username) {
        SseEmitter sseEmitter = emitterMap.get(username);

        log.info("[SseEmitter] Get {}", username);
        return Optional.ofNullable(sseEmitter);
    }

    public void delete(String username) {
        emitterMap.remove(username);
    }
}

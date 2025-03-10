package org.dfbf.soundlink.domain.alert.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AlertRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    public String createEmitterId(Long userId) {
        return String.valueOf(userId) + "_" + System.currentTimeMillis();
    }

    // Redis에 emitterId 저장
    public String saveEmitterId(Long userId, String emitterId) {
        redisTemplate.opsForValue().set("alert::" + userId, emitterId);

        return emitterId;
    }

    // Redis에서 emitterId 조회
    public Optional<String> getEmitterId(Long userId) {
        return Optional.ofNullable((String) redisTemplate.opsForValue().get("alert::" + userId));
    }

    // Redis에 SseEmitter 저장
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        sseEmitterMap.remove(emitterId);
        sseEmitterMap.put(emitterId, sseEmitter);

        return sseEmitter;
    }

    // Redis에서 SseEmitter 조회
    public Optional<SseEmitter> get(String emitterId) {
        return Optional.ofNullable(sseEmitterMap.get(emitterId));
    }

    // Redis에서 emitterId, SseEmitter 삭제
    public void delete(Long userId, String emitterId) {
        sseEmitterMap.remove(emitterId);
        redisTemplate.delete("alert::" + userId);
    }
}

package org.example.soundlinkchat_java.domain.user.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.soundlinkchat_java.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final StringRedisTemplate redisTemplate;

    //가져오기
    public Optional<User> findByUserId(Long userId) {
        String userKey = "user::" + userId;

        try {
            String redisData = redisTemplate.opsForValue().get(userKey);             // Redis에서 사용자 데이터 가져오기

            if (redisData != null) {
                // Redis 데이터가 null이 아니면, JSON을 User 객체로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(redisData, User.class);

                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Redis data", e);
        }
    }
}

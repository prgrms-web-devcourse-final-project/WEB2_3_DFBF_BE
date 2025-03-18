package org.dfbf.soundlink.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${REFRESH_TOKEN_EXPIRATION_TIME}")
    private int REFRESH_EXPIRATION_TIME;

    //가져오기
    public String getRefreshToken(Long userId) {
        String tokenKey = "refreshToken:" + userId;
        return redisTemplate.opsForValue().get(tokenKey);
    }

    //삭제하기
    public void deleteRefreshToken(Long userId){
        String tokenKey = "refreshToken:" + userId;
        redisTemplate.delete(tokenKey);
    }

    //업데이트하기
    public void updateRefreshToken(Long userId, String refreshToken){
        String tokenKey = "refreshToken:" + userId;
        //리프레시 토큰이 존재하지 않을 경우 예외처리.
        if (!redisTemplate.hasKey(tokenKey)) {
            throw new IllegalArgumentException("리프레시 토큰이 존재하지 않습니다. 먼저 저장해야 합니다.");
        }
        redisTemplate.opsForValue().set(tokenKey, refreshToken,REFRESH_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }
}

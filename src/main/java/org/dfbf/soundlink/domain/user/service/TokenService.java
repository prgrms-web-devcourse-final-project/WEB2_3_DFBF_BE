package org.dfbf.soundlink.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    //가져오기
    public String getRefreshToken(Long userId) {
        String tokenKey = "refreshToken:" + userId;
        return redisTemplate.opsForValue().get(tokenKey);
    }

    //저장하기
    public void saveRedisTemplate(Long userId, String refreshToken) {
        String tokenKey = "refreshToken:" + userId;
        redisTemplate.opsForValue().set(tokenKey, refreshToken);
    }

    //삭제하기
    public void deleteRefreshToken(Long userId){
        String tokenKey = "refreshToken:" + userId;
        redisTemplate.delete(tokenKey);
    }
}

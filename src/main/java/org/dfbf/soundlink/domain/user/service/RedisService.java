package org.dfbf.soundlink.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    //인증코드 갱신
    public void setCode(String email, String authCode) {
        ValueOperations<String, String> op = redisTemplate.opsForValue();
        //유효시간 300초
        op.set(email, authCode, 300, TimeUnit.SECONDS);
    }

    public String getCode(String email) throws AuthenticationException {
        ValueOperations<String, String> op = redisTemplate.opsForValue();
        String authCode = op.get(email);
        if(authCode == null){
            throw new AuthenticationException("인증코드가 일치하지 않습니다.");
        }
        return authCode;
    }

}

package org.dfbf.soundlink.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.response.UserStatusDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserStatusSseService userStatusSseService;

    // Redis Key Prefix
    private static final String USER_STATUS_KEY_PREFIX = "user:status:";

    // 유저 상태 저장 (online/chatting/lastActive 등)
    public void saveUserStatus(UserStatusDto status) {
        String key = USER_STATUS_KEY_PREFIX + status.getUserId();
        try {
            String json = objectMapper.writeValueAsString(status);
            redisTemplate.opsForValue().set(key, json);

            // 이게 SSE로 알림 보냄
            userStatusSseService.sendUserStatus(status.getUserId(), status);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // 유저 상태 조회염
    public UserStatusDto getUserStatus(Long userId) {
        String key = USER_STATUS_KEY_PREFIX + userId;
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, UserStatusDto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        // 값이 없으면 기본 오프라인 상태 리턴
        return new UserStatusDto(userId, false, false, 0L);
    }

    // 온라인 처리
    public void setOnline(Long userId) {
        UserStatusDto current = getUserStatus(userId);
        current.setOnline(true);
        current.setLastActive(System.currentTimeMillis());
        saveUserStatus(current);
    }

    // 오프라인 처리
    public void setOffline(Long userId) {
        UserStatusDto current = getUserStatus(userId);
        current.setOnline(false);
        current.setChatting(false);  // 로그아웃하면 당연히 채팅도 종료
        current.setLastActive(System.currentTimeMillis());
        saveUserStatus(current);
    }

     // 채팅 상태 변경
     // chatting=true/false
    public void setChatting(Long userId, boolean chatting) {
        UserStatusDto current = getUserStatus(userId);
        current.setChatting(chatting);
        current.setLastActive(System.currentTimeMillis());
        saveUserStatus(current);
    }

     //몇 분 전에 접속했는지" 계산용
    public long getMinutesSinceLastActive(Long userId) {
        UserStatusDto current = getUserStatus(userId);
        long diffMillis = System.currentTimeMillis() - current.getLastActive();
        return diffMillis / 60000; // 분 단위 변환
    }
}

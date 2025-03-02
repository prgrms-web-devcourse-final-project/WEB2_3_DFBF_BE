package org.dfbf.soundlink.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.entity.redis.ChatRequest;
import org.dfbf.soundlink.domain.emotionRecord.exception.EmotionRecordNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmotionRecordRepository emotionRecordRepository;

    private static final String CHAT_REQUEST_KEY = "chatRequest";

    // 요청을 Redis에 저장 (TTL: 60초)
    public ResponseResult saveRequestToRedis(Long requestUserId, Long emotionRecordId) {
        try {
            // 응답자의 ID를 EmotionRecord에서 가져옴
            Long responseUserId = emotionRecordRepository.findById(emotionRecordId)
                    .orElseThrow(EmotionRecordNotFoundException::new)
                    .getUser()
                    .getUserId();

            // 요청자와 응답자가 같은 경우
            if (requestUserId.equals(responseUserId)) {
                return new ResponseResult(400, "You can't chat with yourself.");
            }

            // Redis에 이미 requestUserId가 포함되어 있는 경우
            if (!redisTemplate.keys(CHAT_REQUEST_KEY + requestUserId + "to*").isEmpty()) {
                String firstKey = redisTemplate.keys(CHAT_REQUEST_KEY + requestUserId + "to*").iterator().next(); // 첫 번째 키 가져오기
                Long ttl = redisTemplate.getExpire(firstKey);
                return new ResponseResult(400, ttl + "초 후에 다시 시도해주세요.");
            }

            // Key & Request 객체 생성
            String key = CHAT_REQUEST_KEY + requestUserId + "to" + emotionRecordId;
            ChatRequest chatRequest = new ChatRequest(requestUserId, responseUserId, emotionRecordId);

            // Redis 저장
            redisTemplate.opsForValue().set(key, chatRequest, Duration.ofSeconds(61));

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD);
        } catch (Exception e) {
            return new ResponseResult(400, "Chat request failed.");
        }
    }

    // 요청을 삭제
    public ResponseResult deleteRequestFromRedis(Long requestUserId, Long emotionRecordId) {
        try {
            // Key 생성
            String key = CHAT_REQUEST_KEY + requestUserId + "to" + emotionRecordId;

            // Redis에 Key가 존재하는 경우 삭제 (KEY가 없는 경우 400)
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.delete(key);
                return new ResponseResult(ErrorCode.SUCCESS);
            } else {
                return new ResponseResult(400, "ChatRequest not found or expired.");
            }

        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD);
        } catch (Exception e) {
            return new ResponseResult(400, "Chat request failed.");
        }
    }

}

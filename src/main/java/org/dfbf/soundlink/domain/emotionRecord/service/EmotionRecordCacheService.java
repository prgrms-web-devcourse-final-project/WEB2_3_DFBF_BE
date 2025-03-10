package org.dfbf.soundlink.domain.emotionRecord.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.emotionRecord.dto.response.EmotionRecordPageResponseDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.response.EmotionRecordResponseMainDTO;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.global.comm.enums.Emotions;
import org.dfbf.soundlink.global.util.CacheKeyGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionRecordCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmotionRecordRepository emotionRecordRepository;

    /**
     * 캐시 조회 및 Fallback 처리
     * 조회 시, 캐시 키를 통해 데이터를 가져오고, 캐시 접근 오류 시,
     * DB에서 데이터를 조회한 결과를 캐시에 저장 후 반환
     */
    public EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> getEmotionRecords(
            Long userId,
            List<String> emotionList,
            String spotifyId,
            int page,
            int size) {

        // emotion 별 개별 키 생성
        List<String> keys = CacheKeyGenerator.generateKeysForEmotions(userId, spotifyId, emotionList, page, size);

        // multiGet을 사용하여 각 키에 해당하는 캐시 데이터를 가져옴
        List<Object> cachedResults = null;

        try {
            cachedResults = redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            log.error("캐시 접근 오류 - keys {}: {}", keys, e.getMessage());
        }

        // 캐시에서 결과가 모두 존재 시, (모든 감정에 대해 캐시 값이 있다면)
        // 여러 감정 조건에 따른 결과(각각 캐싱해 둔 것)를 합쳐서 반환
        if (cachedResults != null && cachedResults.stream().allMatch(Objects::nonNull)) {
            log.info("캐시 hit: {}", keys);

            List<EmotionRecordResponseMainDTO> emotionRecords = new ArrayList<>();
            for (Object cachedResult : cachedResults) {
                @SuppressWarnings("unchecked")
                EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> dto =
                    (EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO>) cachedResult;
                emotionRecords.addAll(dto.records());
            }

            int totalElements = emotionRecords.size();
            int totalPages = (int) Math.ceil(totalElements / (double) size);

            // 결합된 결과와 새로 계산한 페이징 정보를 이용해 최종 DTO 생성
            return new EmotionRecordPageResponseDTO<>(emotionRecords, page, totalPages, totalElements);
        }
        log.info("캐시 miss 또는 일부 캐시 없음 - keys: {}", keys);

        // Fallback(캐싱 실패 시) 처리 : DB에서 전체 데이터를 조회
        EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> dbResult = fetchFromDB(userId, emotionList, spotifyId, page, size);

        // 조회된 결과를 각 개별 캐시 키에 저장
        for (String key : keys) {
            storeInCache(key, dbResult);
        }

        return dbResult;
    }

    /**
     * 주어진 조건(userId, spotifyId, emotions)과 관련된 캐시 키를 모두 무효화(삭제)
     * 페이지(page)와 사이즈(size)도 결과 캐시에 영향을 미치므로 와일드카드(*)를 사용
     */
    public void evictEmotionRecordCache(Long userId, String spotifyId, String emotion) {
        String keyPattern = generateCacheKeyPattern(userId, spotifyId, emotion);

        // 패턴에 매칭되는 모든 키를 가져옴
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (keys != null || !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

   /**
    * 캐시 키 패턴 생성 규칙
    * "user:{userId}:spotifyId:{spotifyId 또는 *}:emotion:{emotion 또는 *}:page:*:size:*"
    */
    private String generateCacheKeyPattern(Long userId, String spotifyId, String emotion) {
        StringBuilder keyPattern = new StringBuilder("user:" + userId);

        // spotifyId가 유효하면 그대로 사용 없으면 와일드 카드(*)
        if (spotifyId != null && !spotifyId.isBlank()) {
            keyPattern.append(":spotify:").append(spotifyId);
        } else {
            keyPattern.append(":spotify*");
        }

        // emotion이 유효하면 그대로 사용 없으면 와일드 카드(*)
        if (emotion != null && !emotion.isEmpty()) {
            keyPattern.append(":emotion:").append(emotion);
        } else {
            keyPattern.append(":emotion:*");
        }

        // 페이지, 사이즈는 결과에 따라 계속 바뀌므로 와일드카드 사용
        keyPattern.append(":page:*:size:*");

        return keyPattern.toString();
    }

    /**
     * DB에서 데이터를 조회
     */
    private EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> fetchFromDB(
            Long userId,
            List<String> emotions,
            String spotifyId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        List<Emotions> emotionEnums = new ArrayList<>();
        if (emotions != null && !emotions.isEmpty()) {
            emotionEnums = emotions.stream()
                    .map(e -> {
                        try {
                            return Emotions.valueOf(e.toUpperCase());
                        } catch (Exception ex) {
                            throw new IllegalArgumentException("잘못된 감정 값이 포함되어 있습니다: " + e);
                        }
                    })
                    .collect(Collectors.toList());
        }
        Page<EmotionRecord> recordsPage = emotionRecordRepository.findByFilters(userId, emotionEnums, spotifyId, pageable);
        List<EmotionRecordResponseMainDTO> dtoList = recordsPage.getContent().stream()
                .map(EmotionRecordResponseMainDTO::fromEntity)
                .collect(Collectors.toList());
        return EmotionRecordPageResponseDTO.fromPage(recordsPage, dtoList);
    }

    /**
     * 캐시에 데이터 저장
     */
    private void storeInCache(String key, EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> dto) {
        try {
            redisTemplate.opsForValue().set(key, dto);
            log.info("캐시 데이터 저장 성공 - key: {}", key);
        } catch (Exception e) {
            log.error("캐시 데이터 저장 중 오류 발생 - key {}: {}", key, e.getMessage());
        }
    }
}

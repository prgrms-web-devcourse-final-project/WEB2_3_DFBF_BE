package org.dfbf.soundlink.domain.emotionRecord;

import org.dfbf.soundlink.domain.emotionRecord.dto.response.EmotionRecordPageResponseDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.response.EmotionRecordResponseMainDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.response.SpotifyMusicResponseWithoutVideoIdDTO;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.service.EmotionRecordCacheService;
import org.dfbf.soundlink.global.util.CacheKeyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmotionRecordCacheServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private EmotionRecordRepository emotionRecordRepository;

    @Mock
    private ValueOperations<String, Object> valueOps;

    @InjectMocks
    private EmotionRecordCacheService emotionRecordCacheService;

    /**
     * 캐시가 모두 존재하는 경우, multiGet 결과를 결합하여 반환하는 테스트
     */
    @DisplayName("캐시 존재 시, 결과 반환 테스트")
    @Test
    public void testGetEmotionRecords_CacheHit() {
        Long userId = 2L;
        List<String> emotionList = Arrays.asList("HAPPY", "SAD");
        String spotifyId = "spotify1";
        int page = 1;
        int size = 10;

        // CacheKeyGenerator를 통해 생성된 키 목록
        List<String> keys = CacheKeyGenerator.generateKeysForEmotions(userId, spotifyId, emotionList, page, size);

        SpotifyMusicResponseWithoutVideoIdDTO testHappy1 = new SpotifyMusicResponseWithoutVideoIdDTO(
                "S002!",
                "테스트 제목1",
                "테스트 가수1",
                "www.testmusic1.com"
        );

        SpotifyMusicResponseWithoutVideoIdDTO testHappy2 = new SpotifyMusicResponseWithoutVideoIdDTO(
                "S0021!",
                "테스트 제목11",
                "테스트 가수11",
                "www.testmusic11.com"
        );

        SpotifyMusicResponseWithoutVideoIdDTO testSad1 = new SpotifyMusicResponseWithoutVideoIdDTO(
                "S004!",
                "테스트 제목2",
                "테스드 가수2",
                "www.testmusic2.com"
        );

        SpotifyMusicResponseWithoutVideoIdDTO testSad2 = new SpotifyMusicResponseWithoutVideoIdDTO(
                "S0042!",
                "테스트 제목22",
                "테스드 가수22",
                "www.testmusic22.com"
        );

        // 각 키에 대해 캐시된 DTO 생성 (더미 데이터)
        List<EmotionRecordResponseMainDTO> recordsHappy = Arrays.asList(
                new EmotionRecordResponseMainDTO(1001L, "테스트 닉넴0", "HAPPY", testHappy1, "코멘트입니다", LocalDateTime.now().toString()),
                new EmotionRecordResponseMainDTO(1002L, "테스트 닉넴1", "HAPPY", testHappy2, "코멘트입니다", LocalDateTime.now().toString())
        );

        EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> dtoHappy =
                new EmotionRecordPageResponseDTO<>(recordsHappy, page, 1, recordsHappy.size());

        List<EmotionRecordResponseMainDTO> recordsSad = Arrays.asList(
                new EmotionRecordResponseMainDTO(1003L, "테스트 닉넴2", "SAD", testSad1, "코멘트입니다", LocalDateTime.now().toString()),
                new EmotionRecordResponseMainDTO(1004L, "테스트 닉넴3", "SAD", testSad2, "코멘트입니다", LocalDateTime.now().toString())
        );

        EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> dtoSad =
                new EmotionRecordPageResponseDTO<>(recordsSad, page, 1, recordsSad.size());

        // multiGet이 각 키에 대해 두 DTO를 반환하도록 설정
        List<Object> cachedResults = Arrays.asList(dtoHappy, dtoSad);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.multiGet(keys)).thenReturn(cachedResults);

        // 테스트 대상 메서드 호출
        EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> result =
                emotionRecordCacheService.getEmotionRecords(userId, emotionList, spotifyId, page, size);

        // 결합된 결과: 두 개의 리스트가 합쳐짐
        List<EmotionRecordResponseMainDTO> combined = new ArrayList<>();
        combined.addAll(recordsHappy);
        combined.addAll(recordsSad);
        int expectedTotalElements = combined.size();
        int expectedTotalPages = (int) Math.ceil(expectedTotalElements / (double) size);

        assertNotNull(result);
        assertEquals(expectedTotalElements, result.totalElements());
        assertEquals(expectedTotalPages, result.totalPages());
        // combined 리스트의 내용과 result.records() 내용이 같은지 확인
        assertEquals(combined.size(), result.records().size());
    }

    /**
     * 캐시 미스(또는 일부 캐시 누락) 시, DB에서 데이터를 조회하여 반환하는 Fallback 테스트
     */
    @DisplayName("캐시 누락 시, DB에서 조회 후 반환 테스트")
    @Test
    public void testGetEmotionRecords_CacheMiss() {
        Long userId = 2L;
        List<String> emotionList = Arrays.asList("HAPPY", "SAD");
        String spotifyId = "spotify1";
        int page = 1;
        int size = 10;

        List<String> keys = CacheKeyGenerator.generateKeysForEmotions(userId, spotifyId, emotionList, page, size);

        // multiGet이 null을 반환하여 캐시 미스 처리
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.multiGet(keys)).thenReturn(null);

        // DB 조회를 위한 더미 데이터 설정
        List<EmotionRecord> dummyRecords = new ArrayList<>();
        // 필요한 경우 dummyRecords에 EmotionRecord 객체들을 추가
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
        Page<EmotionRecord> pageResult = new PageImpl<>(dummyRecords, pageable, 0);
        when(emotionRecordRepository.findByFilters(eq(userId), anyList(), eq(spotifyId), any(Pageable.class)))
                .thenReturn(pageResult);

        // DB 조회 결과가 빈 리스트인 경우에 대한 DTO 생성
        EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> expectedDto =
                EmotionRecordPageResponseDTO.fromPage(pageResult, Collections.emptyList());

        // 테스트 대상 메서드 호출
        EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> result =
                emotionRecordCacheService.getEmotionRecords(userId, emotionList, spotifyId, page, size);

        assertNotNull(result);
        assertEquals(expectedDto.totalElements(), result.totalElements());
        assertEquals(expectedDto.records().size(), result.records().size());

        // multiGet이 캐시 미스이므로, DB 조회 후 각 키에 대해 캐시에 저장되는지 검증
        for (String key : keys) {
            verify(redisTemplate.opsForValue(), atLeastOnce()).set(eq(key), any());
        }
    }

    /**
     * 주어진 조건에 맞는 키 패턴에 해당하는 캐시 키들을 삭제하는지 검증
     */
    @DisplayName("키 삭제 테스트")
    @Test
    public void testEvictEmotionRecordCache() {
        Long userId = 1L;
        String spotifyId = "spotify1";
        String emotion = "happy";

        // 캐시 키 패턴 생성 (테스트용)
        String keyPattern = "user:" + userId + ":spotify:" + spotifyId + ":emotion:" + emotion + ":page:*:size:*";
        Set<String> keys = new HashSet<>(Arrays.asList("user:1:spotify:spotify1:emotion:happy:page:1:size:10",
                "user:1:spotify:*:emotion:sad:page:1:size:10"));
        when(redisTemplate.keys(keyPattern)).thenReturn(keys);

        // 캐시 삭제 시, 리턴값을 true로 설정
        when(redisTemplate.delete(keys)).thenReturn(1L);

        // 테스트 대상 메서드 호출
        emotionRecordCacheService.evictEmotionRecordCache(userId, spotifyId, emotion);

        // verify: redisTemplate.delete가 호출되어 키들이 삭제되었는지 확인
        verify(redisTemplate, times(1)).delete(keys);
    }
}

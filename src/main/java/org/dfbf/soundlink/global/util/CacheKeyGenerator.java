package org.dfbf.soundlink.global.util;

import java.util.ArrayList;
import java.util.List;

// 캐시 관련 유틸 클래스
public class CacheKeyGenerator {
    /**
     * 각 emotion에 대해 개별 캐시 키들을 생성 (데이터 캐싱 및 조회)
     * @param userId 로그인한 유저 ID
     * @param spotifyId 검색 조건 내 spotifyId (required = false)
     * @param emotions 검색 조건 내 감정 리스트 (required = false)
     * @param page 현재 페이지 번호
     * @param size 페이지 사이즈
     * @return 생성된 캐시 키 문자열
     */
    public static List<String> generateKeysForEmotions(Long userId, String spotifyId, List<String> emotions, int page, int size) {
        List<String> keys = new ArrayList<>();

        // spotifyId가 유효하면 해당 값을 사용, 아니면 와일드카드(*)를 사용
        String spotifyPart = (spotifyId != null && !spotifyId.isBlank()) ? spotifyId : "*";

        // 각 emotion에 대해 개별 캐시 키 생성 (캐시 키가 단일 감정만을 포함하도록 함)
        if (emotions != null && !emotions.isEmpty() /*리스트에 요소를 확인하기 위해 isEmpty 사용*/) {
            for (String emotion : emotions) {
                // emotion을 소문자로 변환해서 일관성 유지
                String emotionPart = emotion.toLowerCase();
                String key = "user:" + userId
                        + ":spotifyId:" + spotifyPart
                        + ":emotion:" + emotionPart
                        + ":page:" + page + ":size:" + size;
                keys.add(key);
            }
        } else {
            // emotions가 없을 경우 와일드카드 처리
            String key = "user:" + userId
                    + ":spotifyId:" + spotifyPart
                    + ":emotion:*"
                    + ":page:" + page + ":size:" + size;
            keys.add(key);
        }

        return keys;
    }
}
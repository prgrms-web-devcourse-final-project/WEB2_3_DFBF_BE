package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record EmotionRecordPageResponseDTO<T>(
        List<T> records,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static <T> EmotionRecordPageResponseDTO<T> fromPage(Page<?> recordsPage, List<T> dtoList) {
        return new EmotionRecordPageResponseDTO<>(
                dtoList,
                recordsPage.getNumber() + 1,  // 현재 페이지 번호를 1부터 시작하도록 변경
                recordsPage.getTotalPages(),
                recordsPage.getTotalElements()
        );
    }
}

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
                recordsPage.getNumber(),
                recordsPage.getTotalPages(),
                recordsPage.getTotalElements()
        );
    }
}

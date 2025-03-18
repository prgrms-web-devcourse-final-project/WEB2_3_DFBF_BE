package org.dfbf.soundlink.domain.emotionRecord.dto.request;

public record EmotionRecordUpdateRequestDTO(
        String spotifyId,
        String videoId,
        String title,
        String artist,
        String albumImage,
        String emotion,
        String comment
) {
}

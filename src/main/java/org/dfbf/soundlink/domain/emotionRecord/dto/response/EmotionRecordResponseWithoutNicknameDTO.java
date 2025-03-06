package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public record EmotionRecordResponseWithoutNicknameDTO(
        Long recordId,
        String emotion,
        SpotifyMusicResponseWithoutVideoIdDTO spotifyMusic,
        String comment,
        String createdAt
) {
    public static EmotionRecordResponseWithoutNicknameDTO fromEntity(EmotionRecord record) {
        return new EmotionRecordResponseWithoutNicknameDTO(
                record.getRecordId(),
                record.getEmotion().name(),
                record.getSpotifyMusic() != null ? SpotifyMusicResponseWithoutVideoIdDTO.fromEntity(record.getSpotifyMusic()) : null,
                record.getComment(),
                formatTimestamp(record.getCreatedAt())
        );
    }

    private static String formatTimestamp(Timestamp timestamp) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (timestamp == null) return null;
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}
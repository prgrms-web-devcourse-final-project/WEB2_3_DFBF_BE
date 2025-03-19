package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public record EmotionRecordResponseMainDTO(
        Long recordId,
        String nickName,
        String emotion,
        SpotifyMusicResponseWithoutVideoIdDTO spotifyMusic,
        String comment,
        String createdAt
        ) {

    public static EmotionRecordResponseMainDTO fromEntity(EmotionRecord record) {
        return new EmotionRecordResponseMainDTO(
                record.getRecordId(),
                record.getUser().getNickname(),
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

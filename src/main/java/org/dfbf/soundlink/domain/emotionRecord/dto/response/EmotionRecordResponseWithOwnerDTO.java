package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public record EmotionRecordResponseWithOwnerDTO(
        Long recordId,
        String nickName,
        String emotion,
        SpotifyMusicResponseDTO spotifyMusic,
        String comment,
        String createdAt,
        boolean disable // 유저 본인이면 사용 불가
) {
    public static EmotionRecordResponseWithOwnerDTO fromEntity(EmotionRecord record, Long requestUserId) {
        return new EmotionRecordResponseWithOwnerDTO(
                record.getRecordId(),
                record.getUser().getNickname(),
                record.getEmotion().name(),
                record.getSpotifyMusic() != null ? SpotifyMusicResponseDTO.fromEntity(record.getSpotifyMusic()) : null,
                record.getComment(),
                formatTimestamp(record.getCreatedAt()),
                !record.getUser().getUserId().equals(requestUserId) // 요청한 유저와 저장된 유저 비교
        );
    }

    private static String formatTimestamp(Timestamp timestamp) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (timestamp == null) return null;
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}


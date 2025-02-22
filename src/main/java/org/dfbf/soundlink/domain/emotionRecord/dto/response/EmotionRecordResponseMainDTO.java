package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public record EmotionRecordResponseMainDTO(
        Long recordId,
        String nickName,
        String emotion,
        SpotifyMusicResponseDTO spotifyMusic,
        String comment,
        String createAt
        ) {

    public static EmotionRecordResponseMainDTO fromEntity(EmotionRecord record) {
        return new EmotionRecordResponseMainDTO(
                record.getRecordId(),
                record.getUser().getNickName(),
                record.getEmotion().name(),
                record.getSpotifyMusic() != null ? SpotifyMusicResponseDTO.fromEntity(record.getSpotifyMusic()) : null,
                record.getComment(),
                formatTimestamp(record.getCreatedAt())
        );
    }

  /*  public static List<EmotionRecordResponseMainDTO> fromEntities(List<EmotionRecord> records) {
        return records.stream().map(EmotionRecordResponseMainDTO::fromEntity).collect(Collectors.toList());
    }
*/
    private static String formatTimestamp(Timestamp timestamp) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (timestamp == null) return null;
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}

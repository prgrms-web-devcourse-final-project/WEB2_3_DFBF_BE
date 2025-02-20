package org.dfbf.soundlink.domain.user.dto.response;

import org.dfbf.soundlink.global.comm.enums.Emotions;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public record EmotionRecordDto(Long spotifyId, String title, String artist, String album, Emotions emotion, String comment ,String createdAt) {
    // 커스텀 생성자 -> createdAt을 timestamp에서 String으로
    public EmotionRecordDto(Long spotifyId, String title, String artist, String album, Emotions emotion, String comment, Timestamp createdAt) {
        this(spotifyId, title, artist, album, emotion, comment, formatTimestamp(createdAt));
    }

    private static String formatTimestamp(Timestamp timestamp) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (timestamp == null) return null;
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}

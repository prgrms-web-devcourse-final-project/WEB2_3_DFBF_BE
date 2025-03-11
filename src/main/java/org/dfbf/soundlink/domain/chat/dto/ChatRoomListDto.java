package org.dfbf.soundlink.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public record ChatRoomListDto(
        Long chatRoomId,
        String recordId,
        String nickname,
        String emotion,
        String spotifyId,
        String title,
        String artist,
        String albumImage,
        String videoId,
        String comment,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        Timestamp createdAt
) {
}

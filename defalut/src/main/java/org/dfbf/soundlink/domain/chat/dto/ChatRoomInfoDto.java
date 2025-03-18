package org.dfbf.soundlink.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public record ChatRoomInfoDto(
        String spotifyId,
        String title,
        String artist,
        String albumImage,
        String vedioId,
        String status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        Timestamp createdAt
) {
}

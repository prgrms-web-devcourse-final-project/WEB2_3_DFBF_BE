package org.dfbf.soundlink.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ChatRoomListDto(
        Long chatRoomId,
        String spotifyId,
        String title,
        String artist,
        String albumImage,
        String videoId,
        String comment,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        String createdAt
) {
}

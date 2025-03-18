package org.example.soundlinkchat_java.domain.chat.dto;

import java.util.Date;

public record ChatRoomDto(
        Long chatRoomId,
        Long userId,
        String message,
        Date createdAt
) {
}

package org.example.soundlinkchat_java.domain.chat.dto;

import java.util.Date;

public record ChatDto(
        String chatRoomId,
        Long fromUserId,
        String message,
        Date createdAt
) {
}

package org.example.soundlinkchat_java.domain.chat.dto;

import java.util.Date;

public record ChatResponseDto(
        String chatRoomId,
        Long fromUserId,
        String message,
        Date createdAt,
        boolean isMyMessage
) {}

package org.example.soundlinkchat_java.domain.chat.dto;

public record ExtendConfirmDto(
        String chatRoomId,
        Long requesterUserId,
        String message
) {
}

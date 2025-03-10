package org.dfbf.soundlink.domain.user.dto.response;

public record UserStatusResponseDto(
        Long userId,
        String onlineStatus,
        String chatStatus,
        String lastActiveStr
) {}

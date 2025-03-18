package org.dfbf.soundlink.domain.user.dto.response;

public record UserStatusResponseDto(
        String loginId,
        String onlineStatus,
        String chatStatus,
        String lastActiveStr
) {}

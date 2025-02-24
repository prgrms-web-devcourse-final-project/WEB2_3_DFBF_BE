package org.dfbf.soundlink.domain.blocklist.dto;

import java.sql.Timestamp;

public record BlockRes(
        Long userId,
        Long blockedUserId,
        String nickname,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}

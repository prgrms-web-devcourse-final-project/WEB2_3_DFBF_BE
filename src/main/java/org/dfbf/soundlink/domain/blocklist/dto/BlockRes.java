package org.dfbf.soundlink.domain.blocklist.dto;

import java.sql.Timestamp;

public record BlockRes(
        Long userId,
        Long blockedUserId,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}

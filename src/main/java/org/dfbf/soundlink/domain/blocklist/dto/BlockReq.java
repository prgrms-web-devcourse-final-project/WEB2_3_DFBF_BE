package org.dfbf.soundlink.domain.blocklist.dto;

import java.sql.Timestamp;

public record BlockReq (
        Long userId,
        Long blockedUserId
){
}

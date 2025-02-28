package org.dfbf.soundlink.domain.chat.dto;

import org.dfbf.soundlink.domain.user.entity.User;

public record ChatReqDto (
        Long requestId,
        Long responseId
){}

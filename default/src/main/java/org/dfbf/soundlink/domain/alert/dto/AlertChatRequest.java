package org.dfbf.soundlink.domain.alert.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlertChatRequest(
        @JsonProperty("emotionRecordId")
        Long emotionRecordId,

        @JsonProperty("nickname")
        String nickname) {
}

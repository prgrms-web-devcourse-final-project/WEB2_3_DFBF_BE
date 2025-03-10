package org.dfbf.soundlink.domain.alert.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlertReq(
        String type,
        String timestamp,
        Object data) {
}

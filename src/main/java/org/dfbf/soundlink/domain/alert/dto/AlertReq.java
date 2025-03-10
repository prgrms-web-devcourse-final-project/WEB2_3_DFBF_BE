package org.dfbf.soundlink.domain.alert.dto;

public record AlertReq(
        String type,
        String timestamp,
        Object data) {
}

package org.dfbf.soundlink.domain.alert.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dfbf.soundlink.global.exception.ErrorCode;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Alert {

    @JsonProperty("type")
    private String type;

    @JsonProperty("timestamp")
    private final String timestamp;

    @JsonProperty("data")
    private Object data;

    private String format(Instant now) {
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("UTC"))
                .format(now);
    }

    @Builder
    public Alert(String type, Object data) {
        this.type = type;
        this.timestamp = this.format(Instant.now());
        this.data = data;
    }
}

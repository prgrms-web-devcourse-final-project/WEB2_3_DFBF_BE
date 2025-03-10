package org.dfbf.soundlink.domain.alert.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@Builder
public class Alert {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("timestamp")
    private final String timestamp;

    @JsonProperty("userId")
    private Long userId;

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
        this.data = data != null ? data.toString() : null;
    }
}

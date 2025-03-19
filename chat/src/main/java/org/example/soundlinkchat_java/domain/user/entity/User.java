package org.example.soundlinkchat_java.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("loginId")
    private String loginId;

    @JsonProperty("email")
    private String email;
}
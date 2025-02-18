package org.dfbf.soundlink.global.comm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialType {
    NONE("none"),
    KAKAO("kakao"),
    GOOGLE("google"),
    NAVER("naver");

    private final String type;
}

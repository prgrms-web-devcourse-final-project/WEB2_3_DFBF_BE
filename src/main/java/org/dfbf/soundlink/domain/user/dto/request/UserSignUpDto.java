package org.dfbf.soundlink.domain.user.dto.request;

import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.global.comm.enums.SocialType;

public record UserSignUpDto(
    String nickName,
    Long socialId,
    SocialType socialType,
    String loginId,
    String password,
    String email
) {
    public User toEntity() {
        return User.builder()
            .nickName(nickName)
            .socialId(socialId)
            .socialType(socialType)
            .loginId(loginId)
            .password(password)
            .email(email)
            .build();
    }
}

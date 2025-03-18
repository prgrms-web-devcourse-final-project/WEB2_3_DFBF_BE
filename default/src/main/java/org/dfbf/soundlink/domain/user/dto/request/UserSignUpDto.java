package org.dfbf.soundlink.domain.user.dto.request;

import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.global.comm.enums.SocialType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public record UserSignUpDto(
    String nickName,
    Long socialId,
    SocialType socialType,
    String loginId,
    String password,
    String email
) {
    public User toEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
            .nickName(nickName)
            .socialId(socialId)
            .socialType(socialType)
            .loginId(loginId)
            .password(passwordEncoder.encode(password))
            .email(email)
            .build();
    }
}

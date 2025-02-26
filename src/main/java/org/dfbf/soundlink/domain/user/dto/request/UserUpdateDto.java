package org.dfbf.soundlink.domain.user.dto.request;

import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public record UserUpdateDto(
        String email,
        String loginId,
        String nickName,
        String password,
        String spotifyId,
        String title,
        String artist,
        String albumImage
) { }

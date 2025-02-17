package org.dfbf.soundlink.domain.user.dto.request;

public record UserUpdateDto(
        String email,
        String loginId,
        String nickName,
        String password,
        Long spotifyId,
        String title,
        String artist,
        String albumImage
) {
}

package org.dfbf.soundlink.domain.user.dto.response;

import lombok.*;

@Getter
public class UserMyPageDto {
    private final String loginId;
    private final String nickname;
    private final ProfileMusic profileMusic;

    public UserMyPageDto(String loginId, String nickname, String spotifyId, String title, String artist, String album) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.profileMusic = new ProfileMusic(spotifyId, title, artist, album);
    }
}

// Inner 레코드
record ProfileMusic(String spotifyId, String title, String artist, String album) {}

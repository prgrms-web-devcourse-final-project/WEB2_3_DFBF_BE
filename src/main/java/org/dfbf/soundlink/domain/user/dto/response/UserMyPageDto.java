package org.dfbf.soundlink.domain.user.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;

import java.util.List;

@Getter
public class UserMyPageDto {
    private final String loginId;
    private final String nickname;
    private final ProfileMusic profileMusic;

    public UserMyPageDto(String loginId, String nickname, Long spotifyId, String title, String artist, String album) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.profileMusic = new ProfileMusic(spotifyId, title, artist, album);
    }

//    public UserMyPageDto(String loginId, String nickname, Long profileMusicId) {
//        this.loginId = loginId;
//        this.nickname = nickname;
//        this.profileMusicId = profileMusicId;
//    }
}

// Inner 레코드
record ProfileMusic(Long spotifyId, String title, String artist, String album) {}

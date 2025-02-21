package org.dfbf.soundlink.domain.emotionRecord.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.global.comm.enums.Emotions;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Emotions emotion;

    @ManyToOne
    @JoinColumn(name = "spotify_music_id")
    private SpotifyMusic spotifyMusic;

    private String comment;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public EmotionRecord(User user, Emotions emotion, String comment, SpotifyMusic spotifyMusic) {
        this.user = user;
        this.emotion = emotion;
        this.comment = comment;
        this.spotifyMusic = spotifyMusic;
    }

    // 감정 기록 업데이트 메서드
    public void updateEmotionRecord(String emotion, String comment, SpotifyMusic spotifyMusic) {
        this.emotion = Emotions.valueOf(emotion);
        this.comment = comment;
        this.spotifyMusic = spotifyMusic;
    }
}

/**
 * PERSIST - 부모와 자식엔티티를 한 번에 영속화
 * REMOVE - 함께 저장했던 부모와 자식의 엔티티를 모두 제거할 경우 (고아로 만듭니다)
 * ALL -  CascadeType.PERSIST 와 CascadeType.REMOVE 동시 적용
 * orphanRemoval=true -> 해당 고아 객체를 자동으로 삭제해 주는 옵션
 */

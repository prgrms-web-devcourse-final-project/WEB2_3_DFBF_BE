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
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion")
    private Emotions emotion;

    @ManyToOne
    @JoinColumn(name = "spotify_music_id")
    private SpotifyMusic spotifyMusic;

    @Column(name = "comment")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public EmotionRecord(User user, Emotions emotion, String comment) {
        this.user = user;
        this.emotion = emotion;
        this.comment = comment;
    }
}

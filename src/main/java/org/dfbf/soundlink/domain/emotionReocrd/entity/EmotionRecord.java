package org.dfbf.soundlink.domain.emotionReocrd.entity;

import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.*;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.global.comm.enums.Emotions;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Emotions emotion;

    @ManyToOne
    @JoinColumn(name="spotify_id")
    private SpotifyMusic spotifyMusic;

    private String comment;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public EmotionRecord(User user, Emotions emotion, String comment) {
        this.user = user;
        this.emotion = emotion;
        this.comment = comment;
    }
}

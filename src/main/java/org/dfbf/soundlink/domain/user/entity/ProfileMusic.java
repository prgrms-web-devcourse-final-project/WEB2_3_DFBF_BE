package org.dfbf.soundlink.domain.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileMusic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "profile_music_id")
    private Long profileMusicId;

    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "spotify_id")
    private SpotifyMusic spotifyMusic;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public ProfileMusic(SpotifyMusic sm) {
        this.spotifyMusic = sm;
    }

    public void update(SpotifyMusic spotifyMusic) {
        this.spotifyMusic = spotifyMusic;
    }
}

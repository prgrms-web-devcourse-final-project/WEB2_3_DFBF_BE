package org.dfbf.soundlink.domain.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
public class ProfileMusic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "profile_music_id")
    private Long profileMusicId;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "spotify_id")
    private SpotifyMusic spotifyMusic;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public ProfileMusic(User user, SpotifyMusic spotifyMusic) {
        this.user = user;
        this.spotifyMusic = spotifyMusic;
    }

    public void update(SpotifyMusic spotifyMusic) {
        this.spotifyMusic = spotifyMusic;
    }
}

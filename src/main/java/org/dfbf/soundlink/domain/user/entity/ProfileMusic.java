package org.dfbf.soundlink.domain.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.dfbf.soundlink.domain.emotionReocrd.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.user.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
public class ProfileMusic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileMusicId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "spotify_id")
    private SpotifyMusic spotifyMusic;

    @CreationTimestamp
    private Timestamp createAt;
    @UpdateTimestamp
    private Timestamp updateAt;

    @Builder
    public ProfileMusic(User user, SpotifyMusic spotifyMusic) {
        this.user = user;
        this.spotifyMusic = spotifyMusic;
    }

    public void update(SpotifyMusic spotifyMusic) {
        this.spotifyMusic = spotifyMusic;
    }
}

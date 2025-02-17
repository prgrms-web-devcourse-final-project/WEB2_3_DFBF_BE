package org.dfbf.soundlink.domain.emotionReocrd.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpotifyMusic {
    @Id
    private Long spotifyId;

    private String title;
    private String artist;
    private String albumImage;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updateAt;

    @Builder
    SpotifyMusic (Long spotifyId, String title, String artist, String albumImage) {
        this.spotifyId = spotifyId;
        this.title = title;
        this.artist = artist;
        this.albumImage = albumImage;
    }
}

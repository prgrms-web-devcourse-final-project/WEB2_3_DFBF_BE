package org.dfbf.soundlink.domain.emotionRecord.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spotifyId;

    @Column(name = "title")
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "album_image")
    private String albumImage;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public SpotifyMusic (Long spotifyId, String title, String artist, String albumImage) {
        this.spotifyId = spotifyId;
        this.title = title;
        this.artist = artist;
        this.albumImage = albumImage;
    }
}

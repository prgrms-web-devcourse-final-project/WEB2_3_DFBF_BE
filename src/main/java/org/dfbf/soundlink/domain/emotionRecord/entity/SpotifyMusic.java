package org.dfbf.soundlink.domain.emotionRecord.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpotifyMusic {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    @Column(name = "spotify_id")
    private String spotifyId;

    @Column(name = "video_id")
    private String videoId;

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
    public SpotifyMusic (String spotifyId, String videoId, String title, String artist, String albumImage) {
        this.spotifyId = spotifyId;
        this.videoId = videoId;
        this.title = title;
        this.artist = artist;
        this.albumImage = albumImage;
    }

    public SpotifyMusic (UserUpdateDto userUpdateDto) {
        this.spotifyId = userUpdateDto.spotifyId().get();
        this.title = userUpdateDto.title().get();
        this.artist = userUpdateDto.artist().get();
        this.albumImage = userUpdateDto.albumImage().get();
        this.videoId = userUpdateDto.videoId().get();
    }
}

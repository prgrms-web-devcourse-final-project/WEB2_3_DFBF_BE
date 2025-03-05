package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;

public record SpotifyMusicResponseDTO(String spotifyId, String videoId, String title, String artist, String albumImage) {
    public static SpotifyMusicResponseDTO fromEntity(SpotifyMusic music) {
        return new SpotifyMusicResponseDTO(
                music.getSpotifyId(),
                music.getVideoId(),
                music.getTitle(),
                music.getArtist(),
                music.getAlbumImage()
        );
    }
}

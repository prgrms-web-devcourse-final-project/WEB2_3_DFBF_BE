package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;

public record SpotifyMusicResponseDTO(String spotifyId, String title, String artist, String albumImage) {
    public static SpotifyMusicResponseDTO fromEntity(SpotifyMusic music) {
        return new SpotifyMusicResponseDTO(
                String.valueOf(music.getSpotifyId()),
                music.getTitle(),
                music.getArtist(),
                music.getAlbumImage()
        );
    }
}

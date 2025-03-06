package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;

public record SpotifyMusicResponseWithoutVideoIdDTO(String spotifyId, String title, String artist, String albumImage) {
    public static SpotifyMusicResponseWithoutVideoIdDTO fromEntity(SpotifyMusic music) {
        return new SpotifyMusicResponseWithoutVideoIdDTO(
                music.getSpotifyId(),
                music.getTitle(),
                music.getArtist(),
                music.getAlbumImage()
        );
    }
}

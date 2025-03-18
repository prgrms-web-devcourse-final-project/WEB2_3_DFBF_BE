package org.dfbf.soundlink.domain.emotionRecord.dto.response;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;

public record EmotionRecordUpdateResponseDTO(
        Long recordId,
        String videoId,
        String spotifyId,
        String title,
        String artist,
        String albumImage,
        String emotion,
        String comment
) {
    public static EmotionRecordUpdateResponseDTO fromEntity(EmotionRecord record) {
        return new EmotionRecordUpdateResponseDTO(
                record.getRecordId(),
                record.getSpotifyMusic().getSpotifyId(),
                record.getSpotifyMusic().getVideoId(),
                record.getSpotifyMusic().getTitle(),
                record.getSpotifyMusic().getArtist(),
                record.getSpotifyMusic().getAlbumImage(),
                record.getEmotion().name(),
                record.getComment()
        );
    }
}

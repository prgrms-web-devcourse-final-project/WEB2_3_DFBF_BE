package org.dfbf.soundlink.domain.emotionRecord.repository.dsl;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;

import java.util.List;

public interface SpotifyMusicRepositoryCustom {
    List<SpotifyMusic> findListBySpotifyId(String spotifyId);
}

package org.dfbf.soundlink.domain.emotionRecord.repository;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.dsl.SpotifyMusicRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotifyMusicRepository extends JpaRepository<SpotifyMusic, Long>, SpotifyMusicRepositoryCustom {
    Optional<SpotifyMusic> findBySpotifyId(String spotifyId);
}

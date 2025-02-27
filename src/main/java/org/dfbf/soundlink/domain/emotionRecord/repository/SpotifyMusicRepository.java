package org.dfbf.soundlink.domain.emotionRecord.repository;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotifyMusicRepository extends JpaRepository<SpotifyMusic, Long> {
    Optional<SpotifyMusic> findBySpotifyId(String spotifyId);
}

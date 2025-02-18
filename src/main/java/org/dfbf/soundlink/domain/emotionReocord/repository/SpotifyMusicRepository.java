package org.dfbf.soundlink.domain.emotionReocord.repository;

import org.dfbf.soundlink.domain.emotionReocord.entity.SpotifyMusic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyMusicRepository extends JpaRepository<SpotifyMusic, Long> {
}

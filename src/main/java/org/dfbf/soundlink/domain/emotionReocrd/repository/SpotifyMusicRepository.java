package org.dfbf.soundlink.domain.emotionReocrd.repository;

import org.dfbf.soundlink.domain.emotionReocrd.entity.SpotifyMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyMusicRepository extends JpaRepository<SpotifyMusic, Long> {
}

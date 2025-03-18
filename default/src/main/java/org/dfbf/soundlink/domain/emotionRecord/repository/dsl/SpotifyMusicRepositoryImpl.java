package org.dfbf.soundlink.domain.emotionRecord.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.entity.QSpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class SpotifyMusicRepositoryImpl implements SpotifyMusicRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SpotifyMusic> findListBySpotifyId(String spotifyId) {
        return jpaQueryFactory
                .selectFrom(QSpotifyMusic.spotifyMusic)
                .where(QSpotifyMusic.spotifyMusic.spotifyId.eq(spotifyId))
                .fetch();
    }
}

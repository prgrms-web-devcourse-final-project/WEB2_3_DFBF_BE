package org.dfbf.soundlink.domain.emotionRecord.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.entity.QEmotionRecord;
import org.dfbf.soundlink.domain.user.dto.response.EmotionRecordDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmotionRecordRepositoryImpl implements EmotionRecordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<EmotionRecordDto> findByUser(User user) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        EmotionRecordDto.class,
                        QEmotionRecord.emotionRecord.spotifyMusic.spotifyId,
                        QEmotionRecord.emotionRecord.spotifyMusic.title,
                        QEmotionRecord.emotionRecord.spotifyMusic.artist,
                        QEmotionRecord.emotionRecord.spotifyMusic.albumImage,
                        QEmotionRecord.emotionRecord.emotion,
                        QEmotionRecord.emotionRecord.comment,
                        QEmotionRecord.emotionRecord.createdAt
                ))
                .from(QEmotionRecord.emotionRecord)
                .where(QEmotionRecord.emotionRecord.user.eq(user))
                .fetch();

    }
}

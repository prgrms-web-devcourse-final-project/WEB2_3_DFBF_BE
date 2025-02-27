package org.dfbf.soundlink.domain.emotionRecord.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.QEmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.QSpotifyMusic;
import org.dfbf.soundlink.domain.user.dto.response.EmotionRecordDto;
import org.dfbf.soundlink.domain.user.entity.QUser;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.global.comm.enums.Emotions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    // loginId를 기준으로 JOIN FETCH (user, spotifyMusic) 후 페이징 처리
    @Override
    public Page<EmotionRecord> findByLoginId(String loginId, Pageable pageable) {
        List<EmotionRecord> emotionRecords = jpaQueryFactory
                .selectFrom(QEmotionRecord.emotionRecord)
                .join(QEmotionRecord.emotionRecord.user, QUser.user).fetchJoin()
                .join(QEmotionRecord.emotionRecord.spotifyMusic, QSpotifyMusic.spotifyMusic).fetchJoin()
                .where(QUser.user.loginId.eq(loginId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Spring Data JPA에서 페이징 처리를 위한 메서드 사용 시,
        // 내부적으로 데이터(페이징된 결과)를 가져오는 쿼리와 전체 데이터 수를 계산하는 쿼리가 둘 다 실행됨
        // QueryDSL을 사용할 경우에 위와 달리 데이터 수 계산 쿼리를 별도로 실행해 줘야함 (Querydsl 5 이상 권장 방식)
        return PageableExecutionUtils.getPage(emotionRecords, pageable, () ->
                Optional.ofNullable(
                        jpaQueryFactory
                                .select(QEmotionRecord.emotionRecord.count())
                                .from(QEmotionRecord.emotionRecord)
                                .join(QEmotionRecord.emotionRecord.user, QUser.user)
                                .where(QUser.user.loginId.eq(loginId))
                                .fetchOne()
                ).orElse(0L)
        );
    }

    // 동적으로 필터링 후 EmotionRecords를 가져오는 쿼리
    public Page<EmotionRecord> findByFilters(Long userId, List<Emotions> emotions, String spotifyId, Pageable pageable) {
        List<EmotionRecord> emotionRecords = jpaQueryFactory
                .selectFrom(QEmotionRecord.emotionRecord)
                .join(QEmotionRecord.emotionRecord.user, QUser.user).fetchJoin()
                .leftJoin(QEmotionRecord.emotionRecord.spotifyMusic, QSpotifyMusic.spotifyMusic).fetchJoin()
                .where(
                        excludeUserId(userId),
                        filterByEmotions(emotions),
                        filterBySpotifyId(spotifyId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 위의 findByLoginId 메서드 설명 참고
        return PageableExecutionUtils.getPage(emotionRecords, pageable, () ->
                Optional.ofNullable(
                        jpaQueryFactory
                                .select(QEmotionRecord.emotionRecord.count())
                                .from(QEmotionRecord.emotionRecord)
                                .join(QEmotionRecord.emotionRecord.user, QUser.user)
                                .where(
                                        excludeUserId(userId),          // 로그인 된 userId를 제외한 EmotionRecord 조회
                                        filterByEmotions(emotions),     // 감정이 있을 경우
                                        filterBySpotifyId(spotifyId)    // spotifyId 있을 경우
                                )
                                .fetchOne()
                ).orElse(0L));
    }

    // recordId에 해당하는 EmotionRecord 조회
    @Override
    public Optional<EmotionRecord> findByRecordId(Long recordId) {
        EmotionRecord emotionRecord = jpaQueryFactory
                .selectFrom(QEmotionRecord.emotionRecord)
                .where(QEmotionRecord.emotionRecord.recordId.eq(recordId))
                .fetchOne();
        return Optional.ofNullable(emotionRecord);
    }

    // recordId에 해당하는 EmotionRecord 삭제
    @Override
    public int deleteByRecordId(Long recordId) {
        return (int) jpaQueryFactory
                .delete(QEmotionRecord.emotionRecord)
                .where(QEmotionRecord.emotionRecord.recordId.eq(recordId))
                .execute();
    }

    private BooleanExpression excludeUserId(Long userId) {
        return userId != null ? QUser.user.userId.ne(userId) : null;
    }

    private BooleanExpression filterByEmotions(List<Emotions> emotions) {
        return (emotions != null && !emotions.isEmpty()) ? QEmotionRecord.emotionRecord.emotion.in(emotions) : null;
    }

    private BooleanExpression filterBySpotifyId(String spotifyId) {
        return (spotifyId != null && !spotifyId.isBlank()) ? QSpotifyMusic.spotifyMusic.spotifyId.eq(spotifyId) : null;
    }
}

package org.dfbf.soundlink.domain.user.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.entity.QSpotifyMusic;
import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.dfbf.soundlink.domain.user.entity.QProfileMusic;
import org.dfbf.soundlink.domain.user.entity.QUser;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public UserMyPageDto findUserMyPageDtoByUserId(Long userId) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        UserMyPageDto.class,
                        QUser.user.loginId,
                        QUser.user.nickname,
                        QSpotifyMusic.spotifyMusic.spotifyId,
                        QSpotifyMusic.spotifyMusic.artist,
                        QSpotifyMusic.spotifyMusic.title,
                        QSpotifyMusic.spotifyMusic.albumImage
                ))
                .from(QUser.user)
                .leftJoin(QUser.user.profileMusic, QProfileMusic.profileMusic)
                    .leftJoin(QProfileMusic.profileMusic.spotifyMusic, QSpotifyMusic.spotifyMusic)
                .where(QUser.user.userId.eq(userId))
                .fetchOne();
    }

    @Override
    public Optional<UserMyPageDto> findUserMyPageDtoByLoginId(String loginId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        UserMyPageDto.class,
                        QUser.user.loginId,
                        QUser.user.nickname,
                        QSpotifyMusic.spotifyMusic.spotifyId,
                        QSpotifyMusic.spotifyMusic.title,
                        QSpotifyMusic.spotifyMusic.artist,
                        QSpotifyMusic.spotifyMusic.albumImage
                ))
                .from(QUser.user)
                .leftJoin(QUser.user.profileMusic, QProfileMusic.profileMusic)
                    .leftJoin(QProfileMusic.profileMusic.spotifyMusic, QSpotifyMusic.spotifyMusic)
                .where(QUser.user.loginId.eq(loginId))
                .fetchOne());
    }

    @Override
    public String findPasswordByLoginId(String loginId) {
        return jpaQueryFactory
                .select(QUser.user.password)
                .from(QUser.user)
                .where(QUser.user.loginId.eq(loginId))
                .fetchOne();
    }

    @Override
    @Cacheable(value = "user", key = "#userId", unless = "#result == null")
    public Optional<User> findByUserIdWithCache(Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.userId.eq(userId))
                .fetchOne());
    }

    @Override
    @CachePut(value = "user", key = "#user.userId")
    public User saveWithCache(User user) {
        jpaQueryFactory
                .update(QUser.user)
                .set(QUser.user.nickname, user.getNickname())
                .set(QUser.user.socialType, user.getSocialType())
                .set(QUser.user.socialId, user.getSocialId())
                .set(QUser.user.loginId, user.getLoginId())
                .set(QUser.user.password, user.getPassword())
                .set(QUser.user.email, user.getEmail())
                .where(QUser.user.userId.eq(user.getUserId()))
                .execute();

        return user; // 저장 후 바로 캐싱
    }
}

package org.dfbf.soundlink.domain.user.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.entity.ProfileMusic;
import org.dfbf.soundlink.domain.user.entity.QProfileMusic;
import org.dfbf.soundlink.domain.user.entity.QUser;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileMusicRepositoryImpl implements ProfileMusicCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ProfileMusic> findByUserId(Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(QProfileMusic.profileMusic)
                        .where(QProfileMusic.profileMusic.profileMusicId.eq(
                                queryFactory
                                        .select(QUser.user.profileMusic.profileMusicId) // User의 profileMusicId 사용
                                        .from(QUser.user)
                                        .where(QUser.user.userId.eq(userId)) // User의 userId로 찾기
                                        .fetchOne()
                        ))
                        .fetchOne()
        );
    }


    @Override
    public void deleteByUser(Long userId) {
        queryFactory
                .delete(QProfileMusic.profileMusic)
                .where(QProfileMusic.profileMusic.profileMusicId.eq(
                        queryFactory
                                .select(QUser.user.profileMusic.profileMusicId)
                                .from(QUser.user)
                                .where(QUser.user.userId.eq(userId))
                                .fetchOne()
                )) // User의 userId로 ProfileMusic의 profileMusicId를 삭제
                .execute();
    }

}

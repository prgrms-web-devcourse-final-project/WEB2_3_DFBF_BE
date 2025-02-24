package org.dfbf.soundlink.domain.user.repository;

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
    private final JPAQueryFactory jpaQueryFactory;
    private final QProfileMusic qProfileMusic = QProfileMusic.profileMusic;
    private final QUser qUser = QUser.user;

    @Override
    public Optional<ProfileMusic> findByUserId(Long userId) {
        ProfileMusic result = jpaQueryFactory
                .selectFrom(qProfileMusic)
                .join(qProfileMusic.user, qUser)
                .where(qUser.userId.eq(userId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public void deleteByUser(User user) {
        jpaQueryFactory
                .delete(qProfileMusic)
                .where(qProfileMusic.user.eq(user))
                .execute();
    }
}

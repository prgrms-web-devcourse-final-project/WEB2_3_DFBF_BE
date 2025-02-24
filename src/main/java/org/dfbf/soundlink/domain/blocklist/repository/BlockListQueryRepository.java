package org.dfbf.soundlink.domain.blocklist.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import org.dfbf.soundlink.domain.blocklist.entity.QBlocklist;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BlockListQueryRepository implements BlockListCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QBlocklist qBlocklist = QBlocklist.blocklist;

    @Override
    public List<Blocklist> findAllByUser_UserId(Long userId) {
        return jpaQueryFactory
                .selectFrom(qBlocklist)
                .where(qBlocklist.user.userId.eq(userId))
                .fetch();
    }

    @Override
    public Optional<Blocklist> findByUser_UserIdAndBlockedUser_LoginId(Long userId, String loginId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qBlocklist)
                        .where(
                                qBlocklist.user.userId.eq(userId),
                                qBlocklist.blockedUser.loginId.eq(loginId)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<Blocklist> findByUser_UserIdAndBlockedUser_UserId(Long userId, Long blockedUserId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qBlocklist)
                        .where(
                                qBlocklist.user.userId.eq(userId),
                                qBlocklist.blockedUser.userId.eq(blockedUserId)
                        )
                        .fetchOne()
        );
    }
}

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
                                qBlocklist.user.userId.eq(userId)
                                        .or(qBlocklist.blockedUser.userId.eq(blockedUserId))
                        )
                        .fetchOne()
        );
    }

    @Override
    public Boolean existsByUser_UserIdAndBlockedUser_UserId(Long requestId, Long responseId) {
        return jpaQueryFactory
                .selectOne()
                .from(qBlocklist)
                .where(
                        qBlocklist.user.userId.eq(responseId),
                        qBlocklist.blockedUser.userId.eq(requestId)
                )
                .fetchFirst() != null;
    }
    
    @Override
    public void deleteAllByUser_UserId(Long userId) {
        jpaQueryFactory
                .delete(qBlocklist)
                .where(
                        qBlocklist.user.userId.eq(userId),
                        qBlocklist.blockedUser.userId.eq(userId))
                .execute();
    }
}

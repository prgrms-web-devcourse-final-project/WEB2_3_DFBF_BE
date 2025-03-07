package org.dfbf.soundlink.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.dfbf.soundlink.domain.chat.entity.QChatRoom;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.entity.QProfileMusic;
import org.dfbf.soundlink.domain.user.entity.QUser;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements  ChatRoomCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Long> findChatRoomIdByRequestUserIdAndRecordId(Long requestUserId, Long recordId) {
        return Optional.ofNullable(
                queryFactory
                        .select(QChatRoom.chatRoom.chatRoomId)
                        .from(QChatRoom.chatRoom)
                        .where(
                                QChatRoom.chatRoom.requestUserId.userId.eq(requestUserId)
                                        .and(QChatRoom.chatRoom.recordId.recordId.eq(recordId))
                        )
                        .fetchOne());
    }

    @Override
    public List<ChatRoom> findByRequestUserIdOrderByCreatedAtDesc(Long userId) {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.requestUserId.userId.eq(userId))
                .orderBy(QChatRoom.chatRoom.createdAt.desc())
                .fetch();
    }
}

package org.dfbf.soundlink.domain.chat.repository;

import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository {
    boolean existsByRequestUserIdAndRecordId(User requestUserId, EmotionRecord recordId);
    List<ChatRoom> findByRecordId(EmotionRecord recordId);
}

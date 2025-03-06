package org.dfbf.soundlink.domain.chat.repository;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.entity.User;

import java.util.Optional;

public interface ChatRoomCustomRepository {
    Optional<Long> findChatRoomIdByRequestUserIdAndRecordId(Long requestUserId, Long recordId);
}

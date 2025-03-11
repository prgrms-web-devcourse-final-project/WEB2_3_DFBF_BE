package org.dfbf.soundlink.domain.chat.repository;

import org.dfbf.soundlink.domain.chat.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomCustomRepository {
    Optional<Long> findChatRoomIdByRequestUserIdAndRecordId(Long requestUserId, Long recordId);
    List<ChatRoom> findByRequestUserIdOrderByCreatedAtDesc(Long userId);
    List<ChatRoom> findChatRoomsByUserId(Long userId);

}

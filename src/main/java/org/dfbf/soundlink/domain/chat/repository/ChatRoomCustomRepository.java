package org.dfbf.soundlink.domain.chat.repository;

import java.util.Optional;

public interface ChatRoomCustomRepository {
    Optional<Long> findChatRoomIdByRequestUserIdAndRecordId(Long requestUserId, Long recordId);
}

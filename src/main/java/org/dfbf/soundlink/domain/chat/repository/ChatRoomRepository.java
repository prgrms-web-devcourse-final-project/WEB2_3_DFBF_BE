package org.dfbf.soundlink.domain.chat.repository;

import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}

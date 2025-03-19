package org.example.soundlinkchat_java.domain.chat.repositoty;

import org.example.soundlinkchat_java.domain.chat.dto.ChatDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRepository extends MongoRepository<ChatDto, String> {
    @Query("{ 'chatRoomId': ?0 }")
    List<ChatDto> findByChatRoomId(String chatRoomId);
}

package org.example.soundlinkchat_java.domain.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundlinkchat_java.domain.chat.dto.ChatDto;
import org.example.soundlinkchat_java.domain.chat.dto.ChatResponseDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatConsumer {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "chat-topic",
            groupId = "chat-consumer-group",
            properties = {
                    "spring.json.value.default.type=java.lang.String",
                    "auto.offset.reset=earliest"
            }
    )
    public void consumerChat(String message) {
        try {
            ChatDto dto = objectMapper.readValue(message, ChatDto.class);
            if (dto.chatRoomId() != null) {
                ChatResponseDto responseDto = new ChatResponseDto(
                        dto.chatRoomId(),
                        dto.fromUserId(),
                        dto.message(),
                        dto.createdAt(),
                        false
                );

                simpMessagingTemplate.convertAndSend("/queue/chat-" + dto.chatRoomId(), responseDto);
            }
        } catch (Exception e) {
            log.error("[ChatConsumer] Failed: {}", e.getMessage());
        }
    }
}

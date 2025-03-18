package org.example.soundlinkchat_java.domain.chat.config;

import org.example.soundlinkchat_java.domain.chat.dto.ChatResponseDto;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Date;

@Component
public class WebSocketDisconnectListener {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketDisconnectListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");

        if (userId != null) {
            String chatRoomId = "someChatRoomId";
            ChatResponseDto disconnectMsg = new ChatResponseDto(
                    chatRoomId,
                    userId,
                    "사용자가 연결을 종료하였습니다.",
                    new Date(),
                    false
            );
            messagingTemplate.convertAndSend("/topic/disconnect", disconnectMsg);
        }
    }
}

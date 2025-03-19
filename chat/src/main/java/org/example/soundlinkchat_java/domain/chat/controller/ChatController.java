package org.example.soundlinkchat_java.domain.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundlinkchat_java.domain.chat.dto.*;
import org.example.soundlinkchat_java.domain.chat.enums.ExtendMessageType;
import org.example.soundlinkchat_java.domain.chat.service.ChatService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Map<String, Set<Long>> extensionApprovals = new ConcurrentHashMap<>();


    @MessageMapping("/sendMessage")
    @SendToUser("/queue/mychat")
    public ChatResponseDto sendMessage(Message<?> message, ChatDto incomingDto) {
        Map<String, Object> sessionAttrs =
                (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
        Long userId = (Long) sessionAttrs.get("userId");

        ChatDto safeDto = new ChatDto(
                incomingDto.chatRoomId(),
                userId,
                incomingDto.message(),
                null
        );
        log.info("[WebSocket] Received message: chatRoomId={}, userId={}", safeDto.chatRoomId(), userId);

        ChatDto savedDto = chatService.addMessage(safeDto);

        try {
            String msgJson = objectMapper.writeValueAsString(savedDto);
            kafkaTemplate.send("chat-topic", msgJson);
        } catch (Exception e) {
            log.error("Failed to send to Kafka", e);
        }

        return new ChatResponseDto(
                savedDto.chatRoomId(),
                savedDto.fromUserId(),
                savedDto.message(),
                savedDto.createdAt(),
                true
        );
    }

    @MessageMapping("/extendSession")
    public void extendSession(Message<?> message) {
        Map<String, Object> sessionAttrs = (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
        if (sessionAttrs == null) return;

        // 현재 만료 시점
        Long expireTime = (Long) sessionAttrs.get("expireTime");
        if (expireTime == null) return;

        // 10분 연장
        long extendMs = 10 * 60 * 1000L;
        long newExpireTime = expireTime + extendMs;

        sessionAttrs.put("expireTime", newExpireTime);
        log.info("[WebSocket] 10분 추가 연장 되었습니다. expireTime={}", newExpireTime);
    }


    @MessageMapping("/requestExtend")
    public void requestExtend(Message<?> message, ExtendRequestDto requestDto) {
        Map<String, Object> sessionAttrs = (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
        Long senderUserId = (Long) sessionAttrs.get("userId");
        log.info("[Extend Request] senderUserId = {}, chatRoomId = {}", senderUserId, requestDto.chatRoomId());

        ExtendRequestDto extendedRequest = new ExtendRequestDto(
                requestDto.chatRoomId(),
                "채팅 연장 요청"
        );
        ExtendMessageEnvelope envelope = new ExtendMessageEnvelope(ExtendMessageType.REQUEST, extendedRequest);
        // 대상 사용자의 전용 큐로 전송 (예: /queue/extend-{chatRoomId})
        messagingTemplate.convertAndSend("/queue/extend-" + requestDto.chatRoomId(), envelope);
    }

    @MessageMapping("/confirmExtend")
    public void confirmExtend(Message<?> message, ExtendConfirmDto confirmDto) {
        Map<String, Object> sessionAttrs = (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
        Long responderUserId = (Long) sessionAttrs.get("userId");
        String chatRoomId = confirmDto.chatRoomId();
        log.info("[Extend Confirm] responderUserId = {}, chatRoomId = {}", responderUserId, chatRoomId);

        extensionApprovals.computeIfAbsent(chatRoomId, key -> ConcurrentHashMap.newKeySet())
                .add(responderUserId);

        Set<Long> approvals = extensionApprovals.get(chatRoomId);
        if (approvals.size() >= 2) {
            ExtendConfirmDto finalConfirm = new ExtendConfirmDto(
                    chatRoomId,
                    confirmDto.requesterUserId(),
                    "연장 승인"
            );
            ExtendMessageEnvelope envelope = new ExtendMessageEnvelope(ExtendMessageType.CONFIRM, finalConfirm);
            messagingTemplate.convertAndSend("/topic/extend-" + chatRoomId, envelope);
            extensionApprovals.remove(chatRoomId);
        }
    }
}

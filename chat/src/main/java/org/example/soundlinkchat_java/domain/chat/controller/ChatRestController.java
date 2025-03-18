package org.example.soundlinkchat_java.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.soundlinkchat_java.domain.chat.service.ChatService;
import org.example.soundlinkchat_java.global.exception.ResponseResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRestController {
    private final ChatService chatService;

    @GetMapping("/history")
    @Operation(summary = "채팅방 내 채팅 내역", description = "채팅 그 자체를 가져오는 API (배포서버에서만 작동)")
    public ResponseResult getChatHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam("chatRoomId") String chatRoomId
    ) {
        return chatService.getChatHistoryResponse(chatRoomId, userId);
    }

    @GetMapping("/dev/history")
    @Operation(summary = "채팅방 내 채팅 내역", description = "채팅 그 자체를 가져오는 API (8080포트에서만 작동합니다)")
    public ResponseResult getChatHistoryDev(
            @RequestParam("chatRoomId") String chatRoomId,
            @RequestParam("userId") Long userId
    ) { return chatService.getChatHistoryResponse(chatRoomId, userId); }
}

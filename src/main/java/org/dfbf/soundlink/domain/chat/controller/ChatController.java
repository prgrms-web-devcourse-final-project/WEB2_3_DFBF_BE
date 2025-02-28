package org.dfbf.soundlink.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.dfbf.soundlink.domain.chat.service.ChatRoomService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatRoom")
@Tag(name = "ChatRoom API", description = "채팅방 관련 API")
public class ChatController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    @Operation(summary = "채팅요청 시 채팅방 생성", description = "requestId, responseId 값 확인")
    public ResponseResult create(HttpServletRequest request, @RequestParam Long recordId) {
        return chatRoomService.createChatRoom(request,recordId);
    }

    @PostMapping("/close")
    @Operation(summary = "채팅방 닫기" , description="닫을 시 상태값 'close'변경, 레디스에서 삭제")
    public ResponseResult close(Long chatRoomId) {
        return chatRoomService.closeChatRoom(chatRoomId);
    }
}

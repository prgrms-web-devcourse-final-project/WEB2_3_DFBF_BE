package org.dfbf.soundlink.domain.chat.controller;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.service.ChatRoomService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat API", description = "채팅 관련 API")
public class ChatController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/request")
    @Operation(summary = "채팅 요청 API", description = "채팅 요청 (상세 정보는 노션 API 명세 확인)")
    public ResponseResult requestChat(@AuthenticationPrincipal Long id, @RequestBody Long emotionRecordId) {
        return chatRoomService.saveRequestToRedis(id, emotionRecordId);
    }

    @DeleteMapping("/request")
    @Operation(summary = "채팅 요청 취소 API", description = "채팅 요청 취소")
    public ResponseResult cancelChatRequest(@AuthenticationPrincipal Long id, @RequestBody Long emotionRecordId) {
        return chatRoomService.deleteRequestFromRedis(id, emotionRecordId);
    }

    @PostMapping("/create")
    @Operation(summary = "채팅요청 시 채팅방 생성", description = "requestId, responseId 값 확인")
    public ResponseResult create(@AuthenticationPrincipal Long userId, @RequestParam Long recordId) {
        return chatRoomService.createChatRoom(userId, recordId);
    }

    @PostMapping("/close")
    @Operation(summary = "채팅방 닫기" , description="닫을 시 상태값 'close'변경, 레디스에서 삭제")
    public ResponseResult close(@AuthenticationPrincipal Long userId, Long chatRoomId) {
        return chatRoomService.closeChatRoom(userId, chatRoomId);
    }
}

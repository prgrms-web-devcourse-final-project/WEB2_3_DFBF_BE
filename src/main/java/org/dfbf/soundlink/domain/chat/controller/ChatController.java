package org.dfbf.soundlink.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.dto.ChatRejectDto;
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
    public ResponseResult requestChat(@AuthenticationPrincipal Long id, @RequestParam("recordId") Long emotionRecordId) {
        return chatRoomService.saveRequestToRedis(id, emotionRecordId);
    }

    @DeleteMapping("/request")
    @Operation(summary = "채팅 요청 취소 API", description = "채팅 요청 취소 (요청 보내느사람이 요청 자체를 취소)")
    public ResponseResult cancelChatRequest(@AuthenticationPrincipal Long id, @RequestParam("recordId") Long emotionRecordId) {
        return chatRoomService.deleteRequestFromRedis(id, emotionRecordId);
    }

    @PostMapping("/request/reject")
    @Operation(summary = "채팅 요청 거절 API", description = "채팅 요청 거절 (응답자가 요청 자체를 거절)")
    public ResponseResult rejectChatRequest(@AuthenticationPrincipal Long id, ChatRejectDto chatRejectDto) {
        return chatRoomService.requestRejected(id, chatRejectDto);
    }

    @PostMapping("/create")
    @Operation(summary = "채팅요청 시 채팅방 생성", description = "requestId, responseId 값 확인")
    public ResponseResult create(@AuthenticationPrincipal Long userId, @RequestParam("recordId") Long recordId, @RequestParam("requestNickname") String requestNickname) {
        return chatRoomService.createChatRoom(userId, recordId, requestNickname);
    }

    @PostMapping("/close")
    @Operation(summary = "채팅방 닫기" , description = "닫을 시 상태값 'close'변경, 레디스에서 삭제")
    public ResponseResult close(@AuthenticationPrincipal Long userId, @RequestParam("chatRoomId") Long chatRoomId) {
        return chatRoomService.closeChatRoom(userId, chatRoomId);
    }

    @GetMapping("/room-list")
    @Operation(summary = "채팅 목록 불러오기", description = "채팅 목록 불러옴")
    public ResponseResult roomList(@AuthenticationPrincipal Long userId){
        return chatRoomService.getChatRoomList(userId);
    }

    @GetMapping("/room/detail")
    @Operation(summary = "채팅방 상세 정보", description ="곡정보,상태,생성일을 불러옴")
    public ResponseResult roomInfo(@RequestParam("chatRoomId") Long chatRoomId, @AuthenticationPrincipal Long userId){
        return chatRoomService.getChatRoomInfo(chatRoomId, userId);
    }

    @GetMapping("/history")
    @Operation(summary = "채팅방 내 채팅 내역", description = "채팅 그 자체를 가져오는 API (배포서버에서만 작동)")
    public ResponseResult getChatHistory(@AuthenticationPrincipal Long userId, @RequestParam("chatRoomId") String chatRoomId) {
        return chatRoomService.getChatHistoryResponse(userId, chatRoomId);
    }
}

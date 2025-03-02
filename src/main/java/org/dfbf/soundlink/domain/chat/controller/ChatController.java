package org.dfbf.soundlink.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.service.ChatRoomService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat API", description = "채팅 관련 API")
public class ChatController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/request")
    @Operation(summary = "채팅 요청 API", description = "채팅 요청 (상세 정보는 노션 API 명세 확인)")
    public ResponseResult requestChat(@AuthenticationPrincipal Long id, Long emotionRecordId) {
        return chatRoomService.saveRequestToRedis(id, emotionRecordId);
    }
}

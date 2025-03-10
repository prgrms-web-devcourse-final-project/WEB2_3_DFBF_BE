package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.response.UserStatusDto;
import org.dfbf.soundlink.domain.user.dto.response.UserStatusResponseDto;
import org.dfbf.soundlink.domain.user.service.UserStatusService;
import org.dfbf.soundlink.domain.user.service.UserStatusSseService;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/userStatus")
@RequiredArgsConstructor
@Tag(name = "User Status API", description = "유저 상태 관련 API")
public class UserStatusController {
    private final UserStatusService userStatusService;
    private final UserStatusSseService userStatusSseService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam Long userId) {
        return userStatusSseService.subscribe(userId);
    }

    @GetMapping("/{userId}")
    public ResponseResult getUserStatus(@PathVariable Long userId) {
        UserStatusDto status = userStatusService.getUserStatus(userId);

        boolean isOnline = status.isOnline();
        boolean isChatting = status.isChatting();
        long minutesAgo = userStatusService.getMinutesSinceLastActive(userId);

        UserStatusResponseDto dto = new UserStatusResponseDto(
                userId,
                isOnline ? "ONLINE" : "OFFLINE",
                isChatting ? "CHATTING" : "NOT_CHATTING",
                isOnline
                        ? "현재 접속 중"
                        : (minutesAgo + "분 전에 접속함")
        );
        return new ResponseResult(ErrorCode.SUCCESS, dto);
    }
}

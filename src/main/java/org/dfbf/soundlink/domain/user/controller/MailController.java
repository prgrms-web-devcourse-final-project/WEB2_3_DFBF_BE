package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.domain.user.service.MailService;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 관련 API")
public class MailController {
    private final MailService mailService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Operation(summary = "인증코드 요청", description = "이메일로 인증 코드를 전송.")
    @PostMapping("/verify")
    public ResponseResult requestAuthcode(@RequestParam String email) throws MessagingException {
        return userService.sendAuthCode(email);
    }

    //인증코드 확인
    @Operation(summary="인증코드 확인",description = "사용자가 입력한 인증코드가 유효한지 확인")
    @PostMapping("/verify/check")
    public ResponseResult validateAuthCode(
            @RequestParam String email,
            @RequestParam String authCode) {
        return userService.validateAuthCode(email, authCode);
    }

    //이메일 중복 확인.
    @Operation(summary = "이메일 중복 확인", description="이메일이 이미 사용 중인지 확인")
    @GetMapping("/check-email")
    public ResponseResult checkEmail(@RequestParam String email) {
        return userService.checkEmail(email);
    }
}

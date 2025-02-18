package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.domain.user.service.MailService;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 관련 API")
public class MailController {
    private final MailService mailService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Operation(summary = "인증코드 요청", description = "이메일로 인증 코드를 전송.")
    @GetMapping("/verify")
    public ResponseResult requestAuthcode(@RequestParam String email) throws MessagingException {
        try {
            boolean isSend = userService.sendAuthCode(email);
            return isSend
                    ? new ResponseResult(ErrorCode.SUCCESS,email)
                    : new ResponseResult(ErrorCode.BAD_REQUEST);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    //인증코드 확인
    @Operation(summary="인증코드 확인",description = "사용자가 입력한 인증코드가 유효한지 확인")
    @PostMapping("/verify/check")
    public ResponseResult validateAuthCode(
            @RequestParam String email,
            @RequestParam String authCode) {
        try {
            boolean isSucces = userService.validateAuthCode(email, authCode);
            return isSucces
                    ? new ResponseResult(ErrorCode.SUCCESS,email)
                    : new ResponseResult(ErrorCode.BAD_REQUEST,"이메일 전송 실패: 잘못된 요청입니다.");
        } catch (AuthenticationException e) {
            return new ResponseResult(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    //이메일 중복 확인.
    @Operation(summary = "이메일 중복 확인", description="이메일이 이미 사용 중인지 확인")
    @GetMapping("/check-email")
    public ResponseResult checkEmail(@RequestParam String email) {
        return userService.checkEmail(email);
    }
}
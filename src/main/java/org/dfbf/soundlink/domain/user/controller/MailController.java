package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.service.MailService;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 인증 관련 API")
public class MailController {

    private final MailService mailService; // ✅ 생성자 주입

    @GetMapping("verify/{email}")
    public ResponseResult requestAuthcode(@PathVariable String email) throws MessagingException {
        boolean isSend = mailService.sendSimpleMessage(email);
        return isSend
                ? new ResponseResult(ErrorCode.SUCCESS)
                : new ResponseResult(ErrorCode.BAD_REQUEST);
    }
}
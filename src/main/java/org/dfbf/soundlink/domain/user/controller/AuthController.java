package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.request.LoginReqDto;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API")
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 API")
    public ResponseResult login(@RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {
        return userService.login(loginReqDto, response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 API")
    public ResponseResult logout(HttpServletResponse response, HttpServletRequest request) {
        return userService.logout(response, request);
    }

    @PostMapping("/token")
    @Operation(summary = "토큰 재발급", description="AC 토큰 재발급 API")
    public ResponseResult reissueToken(HttpServletRequest request, HttpServletResponse response){
        return userService.reissueToken(request, response);
    }
}

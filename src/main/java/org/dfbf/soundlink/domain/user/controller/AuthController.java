package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.request.LoginReqDto;
import org.dfbf.soundlink.domain.user.service.KakaoAuthService;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API")
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final KakaoAuthService kakaoAuthService;

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

    // 카카오 로그인 (인가 코드 받아서 회원가입 또는 로그인 진행)
    @Operation(summary = "카카오 로그인", description = "카카오 로그인 후 JWT 발급")
    @GetMapping("/login/kakao")
    public ResponseResult kakaoCallback(@RequestParam String code) {
        return kakaoAuthService.kakaoLogin(code);
    }
}

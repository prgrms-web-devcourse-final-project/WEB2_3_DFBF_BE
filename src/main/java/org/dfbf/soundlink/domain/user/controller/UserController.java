package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.request.LoginReqDto;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "User API", description = "유저 관련 API")
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "일반회원은 Type이 NONE, 나머지는 알맞게 작성해주세요. (대문자로)")
    public ResponseResult signUp(@RequestBody UserSignUpDto userSignUpDto) { return userService.signUp(userSignUpDto); }
    
    @GetMapping("/checkNickName")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임이 이미 사용중인지 확인.")
    public ResponseResult checkNickName(@RequestParam String nickName){
        return userService.checkNickName(nickName);
    }

    @GetMapping("/checkLoginId")
    @Operation(summary = "LoginId 중복 확인", description = "LoginId가 이미 사용중인지 확인.")
    public ResponseResult checkLoginiId(@RequestParam String loginiId){
        return userService.checkLoginiId(loginiId);
    }

    @GetMapping
    @Operation(summary = "유저 조회", description = "유저 조회 API")
    public ResponseResult getUser(@AuthenticationPrincipal Long id) { return userService.getUser(id); }

    @PutMapping
    @Operation(summary = "유저 수정", description = "유저 수정 API")
    public ResponseResult updateUser(@AuthenticationPrincipal Long id,@RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(id, userUpdateDto);
    }

    @DeleteMapping
    @Operation(summary = "유저 삭제", description = "회원 탈퇴하는 API (탈퇴시 프로필 정보도 삭제됩니다.)")
    public ResponseResult deleteUser(@AuthenticationPrincipal Long id) { return userService.deleteUser(id); }

    @GetMapping("/mypage")
    @Operation(summary = "마이 페이지", description = "마이 페이지 조회 API")
    public ResponseResult getMyPage(@AuthenticationPrincipal Long id) { return userService.getMyPage(id); }

    @GetMapping("/profile")
    @Operation(summary = "타 유저 프로필 조회", description = "타 유저 프로필 조회 API")
    public ResponseResult getProfile(@RequestParam("tag") String tag) { return userService.getProfile(tag); }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 API")
    public ResponseResult login(@RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {
        return userService.login(loginReqDto, response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 API")
    public ResponseResult logout(HttpServletResponse response) {
        return userService.logout(response);
    }
}
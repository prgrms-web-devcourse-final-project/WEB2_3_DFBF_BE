package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
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

    @GetMapping
    @Operation(summary = "유저 조회", description = "유저 조회 API")
    public ResponseResult getUser(/*@AuthenticationPrincipal id: Int*/) { return userService.getUser(1L); }

    @PutMapping
    @Operation(summary = "유저 수정", description = "유저 수정 API")
    public ResponseResult updateUser(/*@AuthenticationPrincipal id: Int, */@RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(1L, userUpdateDto);
    }
}
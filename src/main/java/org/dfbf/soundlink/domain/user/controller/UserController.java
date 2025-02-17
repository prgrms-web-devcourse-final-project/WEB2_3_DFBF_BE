package org.dfbf.soundlink.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
public class UserController {
    private final UserService userService;


    @Operation(summary = "닉네임 중복 확인", description = "닉네임이 이미 사용중인지 확인.")
    @GetMapping("/checkNickName")
    public ResponseResult checkNickName(@RequestParam String nickName){
        boolean exists = userService.checkNicName(nickName);
        return exists
                ? new ResponseResult(ErrorCode.DUPLICATE_NICKNAME) //중복 닉네임
                : new ResponseResult(ErrorCode.NOT_DUPLICATE_NICKNAME);
    }
}

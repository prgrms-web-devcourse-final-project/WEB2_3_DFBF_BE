package org.dfbf.soundlink.domain.user.dto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "User API", description = "유저 관련 API")
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public ResponseResult signUp(UserSignUpDto userSignUpDto) { return userService.signUp(userSignUpDto); }
}
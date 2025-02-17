package org.dfbf.soundlink.domain.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.dto.response.UserGetResDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.exception.NoUserDataException;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원가입
    public ResponseResult signUp(UserSignUpDto userSignUpDto) {
        try {
            userRepository.save(userSignUpDto.toEntity());
            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR);
        }
    }

    // 회원 조회
    public ResponseResult getUser(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new NoUserDataException());
            UserGetResDto result = new UserGetResDto(user);

            return new ResponseResult(ErrorCode.SUCCESS, result);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        }
    }
}

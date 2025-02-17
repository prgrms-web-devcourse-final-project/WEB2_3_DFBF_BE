package org.dfbf.soundlink.domain.user.service;

import lombok.AllArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponseResult signUp(UserSignUpDto userSignUpDto) {
        try {
            userRepository.save(userSignUpDto.toEntity());
            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR);
        }

    }
}

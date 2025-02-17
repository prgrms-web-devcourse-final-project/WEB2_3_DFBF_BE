package org.dfbf.soundlink.domain.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MailService mailService;
    private final UserRepository userRepository;
    private final RedisService redisService;

    //인증코드 발급. 이메일이 존재하는지 확인.
    public boolean sendAuthCode(String email)throws MessagingException {
        String authCode = mailService.sendSimpleMessage(email);
        redisService.setCode(email, authCode);
        return true;
    }

    //이메일과 인증코드를 검증
    public boolean validateAuthCode(String email, String authCode) throws AuthenticationException {
        String savedCode = redisService.getCode(email);
        return authCode.equals(savedCode);
    }

    //이메일 중복 확인
    public ResponseResult checkEmail(String email){
        boolean exists = userRepository.existsByEmail(email);
        if(exists){
            return new ResponseResult(ErrorCode.DUPLICATE_EMAIL);
        }
        return new ResponseResult(ErrorCode.NOT_DUPLICATE_EMAIL);
    }

    //닉네임 중복 확인
    public boolean checkNicName(String nicName){
        return userRepository.existsByNickName(nicName);
    }
}

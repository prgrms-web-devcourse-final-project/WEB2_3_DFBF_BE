package org.dfbf.soundlink.domain.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Optional;

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
}

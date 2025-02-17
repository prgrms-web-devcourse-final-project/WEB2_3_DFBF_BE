package org.dfbf.soundlink.domain.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MailService mailService;
    private final UserRepository userRepository;

    //인증코드 발급. 이메일이 존재하는지 확인.
    public boolean sendAuthCode(String email)throws MessagingException {
        String authCode = mailService.sendSimpleMessage(email);
        if(authCode != null){
            Optional<User> opUser = userRepository.findByEmail(email);
            if(opUser.isEmpty()){
                userRepository.save(new User(email, authCode)); //신규 유저 추가
            }else {
                User user = opUser.get();
                user.patch(authCode);   //존재하는 유저일 경우 인증코드 업뎃.
            }
            return true;
        }
        return false;
    }

    //이메일과 인증코드를 검증
    @Transactional
    public boolean validateAuthCode(String email, String authCode){
        Optional<User> opUser = userRepository.findByEmail(email);
        if(opUser.isPresent()){
            User user = opUser.get();
            System.out.println("Before patch: " + user.getAuthCode());
            if(user.getAuthCode().equals(authCode)){
                user.patch(null); //인증코드 삭제
                System.out.println("After patch: " + user.getAuthCode());
                userRepository.flush();
                return true;
            }
        }
        return false;
    }
}

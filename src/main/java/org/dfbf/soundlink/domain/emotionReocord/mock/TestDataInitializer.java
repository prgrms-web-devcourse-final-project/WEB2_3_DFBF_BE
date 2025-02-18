package org.dfbf.soundlink.domain.emotionReocord.mock;

import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
            User testUser = User.builder()
                    .nickName("테스트유저")
                    .socialId(1212L)
                    .socialType("TEST")
                    .loginId("user1")
                    .password("1212")
                    .email("test@example.com")
                    .build();

            userRepository.save(testUser);
    }
}

package org.dfbf.soundlink.domain.emotionRecord.mock;

import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.dfbf.soundlink.global.slack.service.SlackService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final SlackService slackService;

    @Override
    public void run(String... args) {
        // slackService.sendMsg(new ResponseResult(200, "OK"), "흥칫뿡");
        System.out.println("Server ON");
    }
}

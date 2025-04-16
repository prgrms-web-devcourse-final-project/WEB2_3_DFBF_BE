package org.dfbf.soundlink.domain.emotionRecord.mock;

import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
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
        String testJson = """
                    {
                        "code": 0,
                        "message": "string",
                        "timestamp": "string",
                        "data": {}
                    }
                """;
        slackService.sendMsg("{}", "흥칫뿡");
        System.out.println("Server ON");
    }
}

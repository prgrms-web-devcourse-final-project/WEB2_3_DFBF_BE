package org.dfbf.soundlink.global.slack.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SlackService {
    @Value("${SLACK.WEBHOOK.URL}")
    private String webhookUrl = "SLACK_URL";

    public boolean sendMsg(){
        Slack slack = Slack.getInstance();
        String payload = "{\"text\":\"슬랙 메시지 테스트입니다.\"}";
        try {
            WebhookResponse response = slack.send(webhookUrl, payload);
            System.out.println(response);
        } catch (IOException e) {
            log.error("slack 메시지 발송 중 문제가 발생했습니다.", e.toString());
            throw new RuntimeException(e);
        }

        return true;
    }
    출처: https://developer-youn.tistory.com/149 [흔한 컴공 출신 개발자:티스토리]
}

package org.dfbf.soundlink.global.slack.service;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class SlackService {

    @Value("${SLACK.WEBHOOK.URL}")
    private String webhookUrl;

    public void sendMsg(Object object, String errorMessage){
        String message = """
            {
                "blocks": [
                    {
                        "type": "section",
                        "text": {
                            "type": "mrkdwn",
                            "text": "*❗서버 오류*"
                        }
                    },
                    {
                        "type": "section",
                        "text": {
                            "type": "mrkdwn",
                            "text": "ResponseData\n```%s```"
                        }
                    },
                    {
                        "type": "section",
                        "text": {
                            "type": "mrkdwn",
                            "text": "Exeception\n`%s`"
                        }
                    },
                    {
                        "type": "section",
                        "text": {
                            "type": "mrkdwn",
                            "text": "발생시간\n`%s`"
                        }
                    },
                    {
                       "type": "divider"
                    }
                ]
            }
        """.formatted(object, errorMessage, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Slack slack = Slack.getInstance();

        try {
            WebhookResponse response = slack.send(webhookUrl, message);
            System.out.println(response);
        } catch (IOException e) {
            log.error("[SLACK ERROR] ", e.toString());
            throw new RuntimeException(e);
        }
    }
}

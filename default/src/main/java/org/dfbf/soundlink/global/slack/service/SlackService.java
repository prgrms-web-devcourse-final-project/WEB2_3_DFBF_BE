package org.dfbf.soundlink.global.slack.service;

import com.slack.api.Slack;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper objectMapper = new ObjectMapper();
        String objectJson = "";
        try {
            objectJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object)
                    .replace("\"", "");
        } catch (Exception ex) {
            log.error("[SLACK ERROR] Failed to convert object to JSON", ex);
        }

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
                            "text": "Response Data\n```%s```"
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
        """.formatted(objectJson, errorMessage, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            WebhookResponse response = Slack.getInstance().send(webhookUrl, message);

            if (!response.getCode().equals(200)) {
                log.error("[SLACK RESPONSE ERROR] {}", response.getBody());
            }
        } catch (IOException e) {
            log.error("[SLACK ERROR] {}", e.toString());
            throw new RuntimeException(e);
        }
    }
}

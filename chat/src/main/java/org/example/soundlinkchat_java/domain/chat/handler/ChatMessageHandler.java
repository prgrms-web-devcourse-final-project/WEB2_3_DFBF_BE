package org.example.soundlinkchat_java.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.soundlinkchat_java.domain.chat.dto.ChatDto;
import org.example.soundlinkchat_java.global.annotation.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ChatMessageHandler {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterReturning(pointcut = "@annotation(chatMessage)", returning = "returnValue")
    public void sendToKafka(JoinPoint joinPoint, ChatMessage chatMessage, Object returnValue) {
        try {
            if (returnValue instanceof ChatDto chatDto) {
                String msgJson = objectMapper.writeValueAsString(chatDto);
                kafkaTemplate.send("chat-topic", msgJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

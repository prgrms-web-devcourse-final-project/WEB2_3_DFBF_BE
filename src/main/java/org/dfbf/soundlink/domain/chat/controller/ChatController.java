package org.dfbf.soundlink.domain.chat.controller;

import org.dfbf.soundlink.global.annotation.ChatMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @GetMapping("send/message/{message}")
    @ChatMessage
    public String sendMessage(@PathVariable String message) {
        return message;
    }
}

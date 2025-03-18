package org.example.soundlinkchat_java.domain.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConnectionTimeoutInterceptor implements ChannelInterceptor {

    private static final long TIMEOUT_MS = 10 * 60 * 1000L;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            // 처음 연결 시 만료 시간 설정
            long expireTime = System.currentTimeMillis() + TIMEOUT_MS;
            accessor.getSessionAttributes().put("expireTime", expireTime);
            log.info("[WebSocket] CONNECT -> expireTime={}", expireTime);

        } else if (StompCommand.SEND.equals(command)
                || StompCommand.SUBSCRIBE.equals(command)
                || StompCommand.UNSUBSCRIBE.equals(command)) {

            // 매 메시지마다 만료 여부 체크
            Long expireTime = (Long) accessor.getSessionAttributes().get("expireTime");
            if (expireTime != null) {
                long now = System.currentTimeMillis();
                if (now > expireTime) {
                    log.info("[WebSocket] Session timed out. now={} expireTime={}", now, expireTime);
                    throw new IllegalStateException("WebSocket session timed out. 만료되었습니다.");
                }
            }
        }
        return message;
    }
}

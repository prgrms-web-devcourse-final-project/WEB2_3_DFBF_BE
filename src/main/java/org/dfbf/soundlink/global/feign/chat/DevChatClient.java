package org.dfbf.soundlink.global.feign.chat;

import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DevChatClient", url = "http://soundlink.kr:8081/chat")
public interface DevChatClient {

    @GetMapping("/dev/history")
    ResponseResult getChatHistoryDev(@RequestParam("chatRoomId") String chatRoomId,
                                     @RequestParam("userId") Long userId);
}

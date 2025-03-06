package org.dfbf.soundlink.domain.chat.dto;

import org.dfbf.soundlink.domain.chat.entity.ChatRoom;

public class ChatRoomWithVideoResDto {
    private Long chatRoomId;
    private String videoId;

    public ChatRoomWithVideoResDto(Long chatRoomId, String videoId) {
        this.chatRoomId = chatRoomId;
        this.videoId = videoId;
    }
    // Getters
    public Long getChatRoomId() {
        return chatRoomId;
    }

    public String getVideoId() {
        return videoId;
    }
}

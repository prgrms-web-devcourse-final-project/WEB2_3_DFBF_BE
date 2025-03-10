package org.dfbf.soundlink.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusDto {
    private Long userId;
    private boolean online;      // 온라인 여부
    private boolean chatting;    // 채팅 중 여부
    private long lastActive;     // 마지막 활동 시각
}

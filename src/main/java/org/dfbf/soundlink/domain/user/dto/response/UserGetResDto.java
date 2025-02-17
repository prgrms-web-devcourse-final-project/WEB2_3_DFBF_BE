package org.dfbf.soundlink.domain.user.dto.response;

import org.dfbf.soundlink.domain.user.entity.User;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public record UserGetResDto(
        String nickName,
        String email,
        String loginId,
        String createdAt
) {
    public UserGetResDto(User user) {
        this(
                user.getNickName(),
                user.getEmail(),
                user.getLoginId(),
                formatTimestamp(user.getCreatedAt())
        );
    }

    private static String formatTimestamp(Timestamp timestamp) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (timestamp == null) return null;
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}

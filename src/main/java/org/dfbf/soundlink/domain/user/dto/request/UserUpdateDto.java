package org.dfbf.soundlink.domain.user.dto.request;

import java.util.Optional;

public record UserUpdateDto(
        Optional<String> email,
        Optional<String> loginId,
        Optional<String> nickName,
        Optional<String> password,
        Optional<String> spotifyId,
        Optional<String> title,
        Optional<String> artist,
        Optional<String> albumImage,
        Optional<String> videoId
) {
}

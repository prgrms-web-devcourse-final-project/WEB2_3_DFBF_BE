package org.dfbf.soundlink.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Optional;

public record UserUpdateDto(
        Optional<String> email,
        Optional<String> loginId,
        Optional<String> nickName,
        Optional<String> password,
        Optional<String> spotifyId,
        Optional<String> title,
        Optional<String> artist,
        Optional<String> albumImage
) {
    public void toString2() {
        System.out.println("UserUpdateDto(email=" + email + ", loginId=" + loginId + ", nickName=" + nickName + ", password=" + password + ", spotifyId=" + spotifyId);
    }
}

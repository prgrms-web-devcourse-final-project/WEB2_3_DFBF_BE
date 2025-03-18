package org.dfbf.soundlink.domain.user.dto.response;

public record KakaoTokenResponseDTO(
        String token_type,
        String access_token,
        String refresh_token,
        int expires_in,
        int refresh_token_expires_in,
        String scope
) {
}

package org.dfbf.soundlink.domain.user.dto.response;

public record KakaoUserDTO(
        String id,  // id는 Long 타입이지만, 안전성을 위해 String으로 처리
        KakaoAccount kakao_account
) {
    public record KakaoAccount(
            String email,
            String nickname
    ) {
    }
}
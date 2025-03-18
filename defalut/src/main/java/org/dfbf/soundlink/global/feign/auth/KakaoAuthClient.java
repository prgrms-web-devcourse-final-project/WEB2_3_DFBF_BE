package org.dfbf.soundlink.global.feign.auth;

import org.dfbf.soundlink.domain.user.dto.response.KakaoTokenResponseDTO;
import org.dfbf.soundlink.domain.user.dto.response.KakaoUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoAuthClient", url = "https://kauth.kakao.com")
public interface KakaoAuthClient {

    // 카카오 Access Token 요청
    @PostMapping("/oauth/token")
    KakaoTokenResponseDTO getAccessToken(@RequestBody MultiValueMap<String, String> params);

    // 카카오 사용자 정보 요청
    @GetMapping("/v2/user/me")
    KakaoUserDTO getUserInfo(@RequestHeader("Authorization") String accessToken);
}

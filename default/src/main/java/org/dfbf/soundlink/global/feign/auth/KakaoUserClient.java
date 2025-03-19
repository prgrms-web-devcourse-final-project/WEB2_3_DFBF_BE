package org.dfbf.soundlink.global.feign.auth;

import org.dfbf.soundlink.domain.user.dto.response.KakaoUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoUserClient", url = "https://kapi.kakao.com")
public interface KakaoUserClient {
    @GetMapping("/v2/user/me")
    KakaoUserDTO getUserInfo(@RequestHeader("Authorization") String accessToken);
}
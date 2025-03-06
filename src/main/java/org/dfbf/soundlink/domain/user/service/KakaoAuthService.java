package org.dfbf.soundlink.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.response.KakaoTokenResponseDTO;
import org.dfbf.soundlink.domain.user.dto.response.KakaoUserDTO;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.auth.JwtProvider;
import org.dfbf.soundlink.global.auth.client.KakaoAuthClient;
import org.dfbf.soundlink.global.auth.client.KakaoUserClient;
import org.dfbf.soundlink.global.comm.enums.SocialType;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoUserClient kakaoUserClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String domain = "";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${REFRESH_TOKEN_EXPIRATION_TIME}")
    private int REFRESH_TOKEN_EXPIRATION_TIME;

    /**
     *  카카오 로그인 및 JWT 발급
     */
    public ResponseResult kakaoLogin(String code, HttpServletResponse response) {
        // 요청 파라미터 설정 (JSONObject 대신 하나의 Key와 하나 이상의 value로 이루어진 리스트를 쌍으로 받기 위해 LinkedMultiValueMap 사용)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // 카카오 토큰 정보 요청
        KakaoTokenResponseDTO tokenResponse = kakaoAuthClient.getAccessToken(params);
        String accessToken = tokenResponse.access_token();

        // 카카오 사용자 정보 요청
        KakaoUserDTO kakaoUser = kakaoUserClient.getUserInfo("Bearer " + accessToken);
        String kakaoNickname = kakaoUser.kakao_account().nickname();  // 닉네임

        // DB에서 사용자 조회
        Optional<User> existingUser = userRepository.findBySocialId(Long.valueOf(kakaoUser.id()));

        if (existingUser.isPresent()) {
            // 기존 사용자가 존재 시, 로그인 처리
            return generateTokenResponse(existingUser.get(), response);
        } else {
            // 신규 가입은 닉네임 중복 확인 필요
            if (userRepository.existsByNickname(kakaoNickname)) {
                // 기존 닉네임이 중복 시, 새로운 임의의 닉네임 생성
                kakaoNickname = generateUniqueNickname(kakaoNickname);
            }
            // 회원가입
            User newUser = registerNewKakaoUser(kakaoUser, kakaoNickname);
            return generateTokenResponse(newUser, response);
        }
    }

    /**
     *  JWT 토큰 생성
     */
    private ResponseResult generateTokenResponse(User user, HttpServletResponse response) {
        String jwtAccessToken = jwtProvider.createAccessToken(user.getUserId());
        String jwtRefreshToken = jwtProvider.createRefreshToken(user.getUserId());

        //refreshToken - 쿠키
        ResponseCookie refreshCookie = getRefreshToken(jwtRefreshToken);
        response.setHeader("Set-Cookie", refreshCookie.toString());

        //accessToken - 바디
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", jwtAccessToken);

        return new ResponseResult(responseBody);
    }

    /**
     * 새로운 카카오 사용자 회원가입
     */
    private User registerNewKakaoUser(KakaoUserDTO kakaoUser, String nickname) {
        User newUser = User.builder()
                .nickName(nickname)
                .socialId(Long.valueOf(kakaoUser.id()))
                .socialType(SocialType.KAKAO)
                .loginId("kakao_" + kakaoUser.id()) // 카카오 ID 기반 로그인 ID 생성
                .password(passwordEncoder.encode(""))
                .email(kakaoUser.kakao_account().email())
                .build();
        return userRepository.save(newUser);
    }


    /**
     * 닉네임이 중복될 경우 새로운 닉네임 생성
     */
    private String generateUniqueNickname(String baseNickname) {
        int suffix = 1;
        String newNickname = baseNickname;

        // 닉네임이 중복되지 않을 때까지 반복
        while (userRepository.existsByNickname(newNickname)) {
            newNickname = baseNickname + "_" + suffix;
            suffix++;
        }
        return newNickname;
    }

    // RefreshToken을 쿠키로 설정
    private ResponseCookie getRefreshToken(String refreshToken) {
        return ResponseCookie
                .from("REFRESHTOKEN", refreshToken)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(REFRESH_TOKEN_EXPIRATION_TIME/1000) // 만료시간 설정(밀리초 -> 초로 변경)
                .build();
    }
}

package org.dfbf.soundlink.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String accessToken = jwtProvider.resolveAccessToken(request);       //1.Access Token 추출

    if(accessToken !=null && jwtProvider.validateToken(accessToken)) {  //2.유효성 검사
        this.setAuthentication(accessToken);                            //3.유저정보 저장
        }
    filterChain.doFilter(request, response); //필터 체인 진행(전달)
    }

    //유저정보 저장
    public void setAuthentication(String token) {
        Long userId = jwtProvider.getUserId(token); //userId 추출

        // 로그 추가
        System.out.println("Extracted User ID: " + userId);

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        CustomUserDetails userDetails = new CustomUserDetails(userId);

        // 로그 추가
        System.out.println("Created CustomUserDetails: " + userDetails);

        // 인증토큰 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());

        // 인증정보 설정
        System.out.println("Setting authentication with user ID: " + userId); // 로그 추가
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

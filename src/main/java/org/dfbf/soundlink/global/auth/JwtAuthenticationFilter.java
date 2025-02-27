package org.dfbf.soundlink.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = null;
        try {
            accessToken = jwtProvider.resolveAccessToken(request); // 1. Access Token 추출

            if (accessToken != null) {
                // 2. 유효성 검사
                if (!jwtProvider.validateToken(accessToken)) {
                    throw new JwtException("Invalid token");
                }

                // 3. 유저정보 저장
                this.setAuthentication(accessToken);
            }
            filterChain.doFilter(request, response); // 필터 체인 진행(전달)

        } catch (ExpiredJwtException ex) {
            handleException(response, ErrorCode.TOKEN_EXPIRED);

        } catch (JwtException ex) {
            handleException(response, ErrorCode.TOKEN_INVALID);

        } catch (Exception ex) {
            handleException(response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }



    //유저정보 저장
    public void setAuthentication(String token) {
        Long userId = jwtProvider.getUserId(token); //userId 추출

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        CustomUserDetails userDetails = new CustomUserDetails(userId);

        // 인증토큰 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());

        // 인증정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    // 예외 발생 시 JSON 응답을 반환하는 메서드
    private void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ResponseResult responseResult = new ResponseResult(errorCode);

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseResult));
    }

}
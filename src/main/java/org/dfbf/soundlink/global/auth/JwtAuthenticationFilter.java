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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = jwtProvider.resolveAccessToken(request);  // 1. Access Token 추출
        logger.info("Extracted Access Token: {}", accessToken);

        try {
            if (accessToken != null && jwtProvider.validateToken(accessToken)) {  // 2. 유효성 검사
                logger.info("Valid JWT Token found, setting authentication.");
                setAuthentication(accessToken);  // 3. 유저 정보 저장
            } else {
                logger.warn("Invalid JWT Token or token is null");
            }
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT Token: {}", e.getMessage(), e);  // 토큰 만료 예외 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResponseResult(ErrorCode.TOKEN_EXPIRED)));
            return;
        } catch (JwtException e) {
            logger.error("Invalid JWT Token: {}", e.getMessage(), e);  // 유효하지 않은 토큰 예외 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResponseResult(ErrorCode.TOKEN_INVALID)));
            return;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal Argument Exception: {}", e.getMessage(), e);  // 잘못된 인자 예외 처리
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR)));
            return;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage(), e);  // 예상치 못한 예외 처리
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
            return;
        }

        logger.info("Proceeding with filter chain.");
        filterChain.doFilter(request, response);  // 필터 체인 진행
    }

    // 유저 정보 저장
    public void setAuthentication(String token) {
        Long userId = jwtProvider.getUserId(token);  // userId 추출
        logger.info("Extracted userId from JWT: {}", userId);

        if (userId == null) {
            logger.error("userId cannot be null");
            throw new IllegalArgumentException("userId cannot be null");
        }

        CustomUserDetails userDetails = new CustomUserDetails(userId);
        logger.info("UserDetails created for userId: {}", userId);

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());

        // 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("Authentication set for userId: {}", userId);
    }
}

package org.dfbf.soundlink.global.auth;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.user.exception.CustomJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtProvider {

    // 토큰(Access,Refresh) 만료시간(ms)
    @Value("${ACCESS_TOKEN_EXPIRATION_TIME}")
    private long ACCESS_EXPIRATION_TIME;

    @Value("${REFRESH_TOKEN_EXPIRATION_TIME}")
    private long REFRESH_EXPIRATION_TIME;

    //시크릿 키
    SecretKey SECRET_KEY = Keys.hmacShaKeyFor("ee7d4dcf88086125155386d999b3a2258d5c55671a390e608f49a2db31efc6e0".getBytes());

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    //Access 토큰
    public String createAccessToken(long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ACCESS_EXPIRATION_TIME))
                .setHeaderParam("typ", "JWT")
                .signWith(SECRET_KEY,SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_EXPIRATION_TIME))
                .setHeaderParam("typ", "JWT")
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
        try {
            redisTemplate.opsForValue().set("refreshToken:"+userId, refreshToken,REFRESH_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
            return refreshToken;
        } catch (Exception e) {
            System.out.println("[Redis] RefreshToken save failed:" + e.getMessage());
            return null;
        }
    }

    //토큰 검증(변조, 만료, 올바른 형식)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)  // 서명 검증
                    .build()
                    .parseClaimsJws(token);     // 토큰 유효한지 확인 (여기서 만료 시간도 체크)

            // 토큰이 유효한 경우
            return true;
        }catch (ExpiredJwtException e) {
            log.warn("[ERROR] Token is expired.");
            throw new CustomJwtException("토큰이 만료.", e);
        } catch (JwtException e) {
            log.warn("[ERROR] Token validation failed: {}", e.getMessage());
            throw new CustomJwtException("토큰 검증 실패", e);
        } catch (Exception e) {
            throw new CustomJwtException("예기치 않은 오류 발생", e);
        }
    }

    //액세스토큰 추출
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); //토큰을 헤더에 포함했는지
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    //리프레시토큰 추출
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if("REFRESHTOKEN".equals(cookie.getName())){    //REFRESHTOKEN 쿠키 찾아서 해당 값 반환
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 토큰에서 id 반환
    public Long getUserId(String token){
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }
}
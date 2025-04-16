package org.example.soundlinkchat_java.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final RedisTemplate<String, String> redisTemplate;

    // 토큰(Access) 만료시간(ms)
    @Value("${ACCESS_TOKEN_EXPIRATION_TIME}")
    private long ACCESS_EXPIRATION_TIME;

    // 토큰(Refresh) 만료시간(ms)
    @Value("${REFRESH_TOKEN_EXPIRATION_TIME}")
    private long REFRESH_EXPIRATION_TIME;

    @Value("{jwt.secret}")
    private String SECRET_KEY_STRING;

    //시크릿 키
    SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // Access 토큰
    public String createAccessToken(long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ACCESS_EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_EXPIRATION_TIME))
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
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)  //서명 검증
                    .build()
                    .parseClaimsJws(token);     //토큰 유효한지 확인.
            return true;
        } catch (Exception e) {
            System.out.println("[ERROR] Token validation failed: ");
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token); // 만료된 토큰을 처리하려면 ExpiredJwtException이 발생함
            return false; // 만료되지 않으면 false
        } catch (ExpiredJwtException ex) {
            return true; // 만료된 경우 true
        } catch (Exception ex) {
            return false; // 다른 예외는 false
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
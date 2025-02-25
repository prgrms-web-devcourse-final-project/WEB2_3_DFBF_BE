package org.dfbf.soundlink.global.auth;

import ch.qos.logback.core.subst.Token;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.dfbf.soundlink.domain.user.exception.ExpiredTokenException;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtProvider {

    // 토큰(Access,Refresh) 만료시간(ms)
    @Value("${ACCESS_TOKEN_EXPIRATION_TIME}")
    private long ACCESS_EXPIRATION_TIME;

    @Value("${REFRESH_TOKEN_EXPIRATION_TIME}")
    private long REFRESH_EXPIRATION_TIME;

    //시크릿 키 자동 생성
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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
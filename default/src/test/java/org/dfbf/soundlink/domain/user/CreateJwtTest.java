package org.dfbf.soundlink.domain.user;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CreateJwtTest {

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    @Test
    void secretKeyValid(){
        assertThat(secretKeyPlain).isNotNull();
    }

    @Test
//    @DisplayName("secretKey 원문으로 hmac 암호화 알고리즘에 맞는 SecretKey 객체를 만들 수 있다.")
    void t2() {
        // secretKeyPlain이 Base64 인코딩된 상태라면 디코딩하여 사용해야 함
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyPlain);

        // HMAC 서명용 SecretKey 생성
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        assertThat(secretKey).isNotNull();
    }
}

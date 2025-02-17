package org.dfbf.soundlink.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String nickName;
    private Long socialId;
    private String socialType;
    private String loginId;
    private String password;
    private String email;

    // 인증코드
    private String authCode;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updateAt;

    @Builder
    User(String nickName, Long socialId, String socialType, String loginId, String password, String email) {
        this.nickName = nickName;
        this.socialId = socialId;
        this.socialType = socialType;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
    }

    @Builder
    public User(String email, String authCode){
        this.email = email;
        this.authCode = authCode;
    }

    public void patch(String authCode){
        this.authCode = authCode;
    }
}

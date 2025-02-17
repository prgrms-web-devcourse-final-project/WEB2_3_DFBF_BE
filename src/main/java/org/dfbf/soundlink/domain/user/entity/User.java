package org.dfbf.soundlink.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dfbf.soundlink.global.comm.enums.SocialType;
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

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = true)
    private Long socialId;

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
    User(String nickName, Long socialId, SocialType socialType, String loginId, String password, String email) {
        this.nickName = nickName;
        this.socialId = socialId;
        this.socialType = socialType;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
    }
}

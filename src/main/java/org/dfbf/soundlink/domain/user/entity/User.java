package org.dfbf.soundlink.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.dfbf.soundlink.global.comm.enums.SocialType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true)
    private String nickName;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = true)
    private Long socialId;

    @Column(name="login_id")
    private String loginId;

    private String password;
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Blocklist> blocklist;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<EmotionRecord> emotionRecord;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private ProfileMusic profileMusic;

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

    public void update(UserUpdateDto userUpdateDto, BCryptPasswordEncoder passwordEncoder) {
        this.nickName = userUpdateDto.nickName();
        this.loginId = userUpdateDto.loginId();
        this.password = passwordEncoder.encode(userUpdateDto.password());
        this.email = userUpdateDto.email();
    }
}

/**
 * PERSIST - 부모 엔티티가 영속화(저장)될 때, 자식 엔티티도 자동으로 저장
 * MERGE - 부모 엔티티가 병합될 때, 자식 엔티티도 자동으로 병합
 * REMOVE - 부모 엔티티가 삭제될 때, 자식 엔티티도 자동으로 삭제
 * REFRESH - 부모 엔티티가 새로고침(refresh)될 때, 자식 엔티티도 자동으로 새로고침
 * DETACH - 부모 엔티티가 분리(detach)될 때, 자식 엔티티도 자동으로 분리
 * ALL -  모든 작업을 자식 엔티티에 전파
 */

/**
 * CascadeType.REMOVE -> 부모 엔티티가 삭제될 때 자식 엔티티는 삭제
 * orphanRemoval = true -> 자식 엔티티가 부모와의 관계에서 제거될 때 삭제
 * 가끔 사용하지 않는 데이터가 DB에 남아있는 경우가 있는데, 이를 방지하기 위해 사용
 * 혹은 User쪽에 있는 리스트에서 제거할 경우, 양방향 매핑이기 떄문에 자식이 고아가 댐 -> 이를 위헤서  orphanRemoval를 사용
 * 근데 우린 사용 안하니까 사실 빼도 무방할 듯....
 */
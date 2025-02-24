package org.dfbf.soundlink.domain.user.repository;

import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, org.dfbf.soundlink.domain.user.repository.dsl.UserRepositoryCustom {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    boolean existsByLoginId(String loginId);

    Optional<User> findById(Long id);

    // 로그인관련
    Optional<User> findByLoginId(String loginId);
    
    // 비밀번호만 조회
    @Query("Select u.password from User u  where u.loginId =:loginId ")
    String findPasswordByLoginId(@Param("loginId")String loginId);
}

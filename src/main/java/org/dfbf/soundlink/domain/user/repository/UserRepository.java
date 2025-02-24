package org.dfbf.soundlink.domain.user.repository;

import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.dsl.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    boolean existsByLoginId(String loginId);

    Optional<User> findById(Long id);

    Optional<User> findByLoginId(String loginId);
}

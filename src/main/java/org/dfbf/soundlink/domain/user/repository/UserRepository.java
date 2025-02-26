package org.dfbf.soundlink.domain.user.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.dsl.UserRepositoryCustom;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}

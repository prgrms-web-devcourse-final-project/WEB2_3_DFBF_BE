package org.dfbf.soundlink.domain.user.repository.dsl;

import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepositoryCustom {

    UserMyPageDto findUserMyPageDtoByUserId(Long userId);

    Optional<UserMyPageDto> findUserMyPageDtoByLoginId(@Param("loginId") String loginId);

    String findPasswordByLoginId(String loginId);
}

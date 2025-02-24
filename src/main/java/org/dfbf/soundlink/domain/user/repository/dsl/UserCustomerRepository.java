package org.dfbf.soundlink.domain.user.repository.dsl;

import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserCustomerRepository {

    UserMyPageDto findMyPageDtoByUserId(@Param("user") User user);

    Optional<UserMyPageDto> findMyPageDtoByLoginId(@Param("loginId") String loginId);
}

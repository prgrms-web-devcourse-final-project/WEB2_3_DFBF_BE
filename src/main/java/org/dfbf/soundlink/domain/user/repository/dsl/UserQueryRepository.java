package org.dfbf.soundlink.domain.user.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository implements UserCustomerRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    Optional<UserMyPageDto> findMyPageDtoByLoginId(String loginId) {
        return Optional.empty();
    }
}

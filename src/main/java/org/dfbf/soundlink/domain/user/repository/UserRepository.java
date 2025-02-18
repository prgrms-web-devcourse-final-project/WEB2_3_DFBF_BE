package org.dfbf.soundlink.domain.user.repository;

import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);

    Optional<User> findById(Long id);

    // JPQL에서는 Inner Class에 직접 값을 넣을 수 있도록 하는 기능은 지원하지 않는다.
    @Query(
            "SELECT new org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto(" +
                    "u.email, u.loginId, u.nickName," +
                    "new org.dfbf.soundlink.domain.user.dto.response.ProfileMusic(" +
                    "pm.spotifyMusic.spotifyId, pm.spotifyMusic.title, pm.spotifyMusic.artist, pm.spotifyMusic.albumImage)) " +
                    "FROM User u " +
                    "JOIN FETCH ProfileMusic pm ON pm.user = u " +
                    "WHERE u = :user"
    )
    UserMyPageDto findMyPageDtoByUserId(@Param("user") User user);
}

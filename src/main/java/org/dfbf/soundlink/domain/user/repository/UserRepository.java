package org.dfbf.soundlink.domain.user.repository;

import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


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

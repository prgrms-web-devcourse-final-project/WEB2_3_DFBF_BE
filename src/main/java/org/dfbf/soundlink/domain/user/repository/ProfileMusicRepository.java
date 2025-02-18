package org.dfbf.soundlink.domain.user.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.dfbf.soundlink.domain.user.entity.ProfileMusic;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileMusicRepository extends JpaRepository<ProfileMusic, Long> {

    @Query("SELECT p FROM ProfileMusic p WHERE p.user.userId = :userId" )
    Optional<ProfileMusic> findByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProfileMusic p WHERE p.user = :user")
    public void deleteByUser(@Param("user") User user);
}

/**
 * @Modifying(clearAutomatically = true)
 * 1차 캐시안의 내용까지 지워버리는 옵션
 */

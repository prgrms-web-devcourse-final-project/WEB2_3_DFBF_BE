package org.dfbf.soundlink.domain.user.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.dfbf.soundlink.domain.user.entity.ProfileMusic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileMusicRepository extends JpaRepository<ProfileMusic, Long> {
    @Query("SELECT p FROM ProfileMusic p WHERE p.user.userId = :userId" )
    Optional<ProfileMusic> findByUserId(@Param("userId") Long userId);
}

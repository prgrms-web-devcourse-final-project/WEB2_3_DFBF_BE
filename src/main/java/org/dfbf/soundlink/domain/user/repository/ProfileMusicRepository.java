package org.dfbf.soundlink.domain.user.repository;


import org.dfbf.soundlink.domain.user.entity.ProfileMusic;
import org.dfbf.soundlink.domain.user.repository.dsl.ProfileMusicCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileMusicRepository extends JpaRepository<ProfileMusic, Long>, ProfileMusicCustomRepository {
}

/**
 * @Modifying(clearAutomatically = true)
 * 1차 캐시안의 내용까지 지워버리는 옵션
 */

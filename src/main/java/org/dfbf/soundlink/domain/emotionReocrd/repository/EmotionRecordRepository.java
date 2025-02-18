package org.dfbf.soundlink.domain.emotionReocrd.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.dfbf.soundlink.domain.emotionReocrd.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM EmotionRecord e WHERE e.user = :user")
    public void deleteByUser(@Param("user") User user);
}

/**
 * @Modifying(clearAutomatically = true)
 * 1차 캐시안의 내용까지 지워버리는 옵션
 */


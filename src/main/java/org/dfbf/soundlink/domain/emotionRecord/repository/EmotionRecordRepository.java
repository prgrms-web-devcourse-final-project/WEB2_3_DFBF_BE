package org.dfbf.soundlink.domain.emotionRecord.repository;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.repository.dsl.EmotionRecordRepositoryCustom;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long>, EmotionRecordRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM EmotionRecord e WHERE e.user = :user")
    void deleteByUser(@Param("user") User user);
}

/*
@Modifying(clearAutomatically = true)
1차 캐시안의 내용까지 지워버리는 옵션
*/



package org.dfbf.soundlink.domain.emotionReocord.repository;

import org.dfbf.soundlink.domain.emotionReocord.entity.EmotionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long> {
}

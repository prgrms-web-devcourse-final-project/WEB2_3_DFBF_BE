package org.dfbf.soundlink.domain.emotionRecord.repository.dsl;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.dto.response.EmotionRecordDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmotionRecordRepositoryCustom {

    List<EmotionRecordDto> findByUser(User user);

    Page<EmotionRecord> findByLoginId(String loginId, Pageable pageable);

    Page<EmotionRecord> findByWithoutUserId(Long userId, Pageable pageable);

    Optional<EmotionRecord> findByRecordId(Long recordId);

    int deleteByRecordId(Long recordId);
}

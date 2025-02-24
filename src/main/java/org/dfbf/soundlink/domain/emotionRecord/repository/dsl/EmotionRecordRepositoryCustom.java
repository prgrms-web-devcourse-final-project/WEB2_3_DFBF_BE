package org.dfbf.soundlink.domain.emotionRecord.repository.dsl;

import org.dfbf.soundlink.domain.user.dto.response.EmotionRecordDto;
import org.dfbf.soundlink.domain.user.entity.User;

import java.util.List;

public interface EmotionRecordRepositoryCustom {

    List<EmotionRecordDto> findByUser(User user);
}

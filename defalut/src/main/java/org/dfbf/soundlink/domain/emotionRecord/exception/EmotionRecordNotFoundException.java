package org.dfbf.soundlink.domain.emotionRecord.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class EmotionRecordNotFoundException extends BusinessException {
    public EmotionRecordNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD);
    }
}

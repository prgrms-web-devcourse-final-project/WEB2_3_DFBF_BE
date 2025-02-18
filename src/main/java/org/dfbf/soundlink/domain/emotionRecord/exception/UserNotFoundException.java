package org.dfbf.soundlink.domain.emotionRecord.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_USER);
    }
}

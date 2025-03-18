package org.dfbf.soundlink.domain.chat.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class UnauthorizedAccessException extends BusinessException {
    public UnauthorizedAccessException() {
        super(ErrorCode.CHAT_UNAUTHORIZED);
    }
}

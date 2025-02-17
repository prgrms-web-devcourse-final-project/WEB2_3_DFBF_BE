package org.dfbf.soundlink.domain.user.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class NoUserDataException extends BusinessException {
    public NoUserDataException() {
        super(ErrorCode.FAIL_TO_FIND_USER);
    }
}

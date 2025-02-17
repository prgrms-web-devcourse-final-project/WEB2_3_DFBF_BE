package org.dfbf.soundlink.domain.blocklist.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class BlockingUserNotFound extends BusinessException {
    public BlockingUserNotFound() {
        super(ErrorCode.BLOCKING_USER_NOT_FOUND);
    }
}

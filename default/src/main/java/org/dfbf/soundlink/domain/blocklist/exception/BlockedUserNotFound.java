package org.dfbf.soundlink.domain.blocklist.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class BlockedUserNotFound extends BusinessException {
    public BlockedUserNotFound() {
        super(ErrorCode.BLOCKED_USER_NOT_FOUND);
    }
}

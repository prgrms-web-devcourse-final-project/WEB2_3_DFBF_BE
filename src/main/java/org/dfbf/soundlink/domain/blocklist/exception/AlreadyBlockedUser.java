package org.dfbf.soundlink.domain.blocklist.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class AlreadyBlockedUser extends BusinessException {
    public AlreadyBlockedUser() {
        super(ErrorCode.ALREADY_BLOCKED_USER);
    }
}

package org.dfbf.soundlink.domain.chat.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class ChatRoomNotFoundException extends BusinessException {
    public ChatRoomNotFoundException() {super(ErrorCode.CHATROOM_NOT_FOUND); }
}

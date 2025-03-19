package org.example.soundlinkchat_java.global.exception;

import lombok.Getter;

@Getter
public class DbConnectionException extends BusinessException {
    public DbConnectionException() {
        super(ErrorCode.DB_ERROR);
    }
}

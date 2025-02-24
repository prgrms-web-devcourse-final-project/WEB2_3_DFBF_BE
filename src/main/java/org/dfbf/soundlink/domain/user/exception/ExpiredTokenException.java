package org.dfbf.soundlink.domain.user.exception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message){
        super(message);
    }
}

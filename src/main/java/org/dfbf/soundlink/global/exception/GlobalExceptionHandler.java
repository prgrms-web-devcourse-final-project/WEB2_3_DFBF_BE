package org.dfbf.soundlink.global.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult handleValidationExceptions(MethodArgumentNotValidException ex) {
        return new ResponseResult(ErrorCode.BAD_REQUEST, ex.getBindingResult().getAllErrors().toString());
    }
}

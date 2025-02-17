package org.dfbf.soundlink.global.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class ResponseResult {
    private final int code;
    private final String message;
    private final String timestamp;
    private Object data;

    /**
     * 1. 정상 (200, success)
     * -> ResponseResult(ErrorCode.SUCCESS)
     * -> ResponseResult(200, "success")
     *
     * 2. 정상 & Body에 데이터가 있는 경우
     * -> ResponseResult(ErrorCode.SUCCESS, data)
     *
     * 3. 에러 발생한 경우
     * -> ResponseResult(ErrorCode.DB_ERROR)
     * -> ResponseResult(500, "DB 연결 에러")
     */

    @Builder
    public ResponseResult(ErrorCode errorCode){
        this.code = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
        this.timestamp = Instant.now().toString();
    }

    @Builder
    public ResponseResult(ErrorCode errorCode, Object data){
        this.code = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
        this.timestamp = Instant.now().toString();
        this.data = data;
    }

    @Builder
    public ResponseResult(Object data){
        this.code = ErrorCode.SUCCESS.getStatus().value();
        this.message = ErrorCode.SUCCESS.getMessage();
        this.timestamp = Instant.now().toString();
        this.data = data;
    }
}

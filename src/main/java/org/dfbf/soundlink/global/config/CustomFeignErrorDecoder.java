package org.dfbf.soundlink.global.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    private static final Logger log = LoggerFactory.getLogger(CustomFeignErrorDecoder.class);
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        log.error("Feign 요청 실패: methodKey={}, status={}", methodKey, status);

        return switch (status) {
            case UNAUTHORIZED ->  // 401
                    new BusinessException(ErrorCode.KAKAO_API_UNAUTHORIZED);
            case FORBIDDEN ->     // 403
                    new BusinessException(ErrorCode.KAKAO_API_FORBIDDEN);
            case BAD_REQUEST ->   // 400
                    new BusinessException(ErrorCode.KAKAO_API_BAD_REQUEST);
            case INTERNAL_SERVER_ERROR -> // 500
                    new BusinessException(ErrorCode.KAKAO_API_SERVER_ERROR);
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}

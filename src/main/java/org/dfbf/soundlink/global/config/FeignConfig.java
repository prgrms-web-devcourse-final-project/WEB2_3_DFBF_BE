package org.dfbf.soundlink.global.config;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    // 로그 레벨 설정 (요청/응답 로깅)
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.HEADERS; // 바디 제외
    }

    // 타임아웃 설정 (연결 5초, 읽기 5초)
    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(5000, TimeUnit.MILLISECONDS, 5000, TimeUnit.MILLISECONDS, true);
    }

    // 에러 디코더 설정 (예외 처리)
    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}

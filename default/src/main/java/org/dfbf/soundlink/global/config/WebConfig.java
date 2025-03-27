package org.dfbf.soundlink.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 스프링부트 5.3 이후,
     * allowCredentials가 true일 때, allowedOrigins에 특수 값인 "*" 추가할 수 없게 되었다.
     * 대신 allowOriginPatterns를 사용해야 한다.
     */

    // Develop에 적용
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트
                .allowedOriginPatterns("*")       // 모든 도메인 허용 // NOSONAR
                .allowedMethods("*")          // 모든 HTTP 메서드 허용
                .allowedHeaders("*")          // 모든 헤더 허용
                .exposedHeaders("*")          // 모든 응답 헤더 허용
                .allowCredentials(true)     // 인증 정보는 제외
                .exposedHeaders("Authorization");
    }
}

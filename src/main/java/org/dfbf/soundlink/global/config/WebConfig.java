package org.dfbf.soundlink.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    //    private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:3000";
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins(DEVELOP_FRONT_ADDRESS)
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .exposedHeaders("location")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트
                .allowedOrigins("*")          // 모든 도메인 허용
                .allowedMethods("*")          // 모든 HTTP 메서드 허용
                .allowedHeaders("*")          // 모든 헤더 허용
                .exposedHeaders("*")          // 모든 응답 헤더 허용
                .allowCredentials(false);     // 인증 정보는 제외
    }
}

package org.example.soundlinkchat_java.global.config;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.soundlinkchat_java.global.auth.JwtAuthenticationFilter;
import org.example.soundlinkchat_java.global.auth.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .requireCsrfProtectionMatcher(ServletRequest::isSecure) // HTTPS만 CSRF 보호 적용
                )
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/**").permitAll()
//                        .requestMatchers("/swagger-ui/**").permitAll()
//                        .requestMatchers("/v3/api-docs/**").permitAll()
//                        .requestMatchers("/").permitAll()
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )
                // JwtAuthenticationFilter 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
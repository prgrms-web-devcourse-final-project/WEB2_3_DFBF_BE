package org.example.soundlinkchat_java.global.config;

import lombok.RequiredArgsConstructor;
import org.example.soundlinkchat_java.global.auth.JwtAuthenticationFilter;
import org.example.soundlinkchat_java.global.auth.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings("squid:S4502")
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
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

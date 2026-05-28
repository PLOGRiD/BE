package com.seohaeng.backend.global.security;

import com.seohaeng.backend.global.security.jwt.JwtAuthenticationFilter;
import com.seohaeng.backend.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ALLOW_AUTH_URL = "/api/v1/users/auth/**";

    private static final String[] SECURITY_ALLOW_ARRAY = { //인증 없이 접근 가능한 엔드 포인트
            ALLOW_AUTH_URL,
            //등등 추가 및 수정해주세요
            "/api/v1/common/health/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/ws/**",
            "/topic/**",
            "/favicon.ico"
    };

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers(SECURITY_ALLOW_ARRAY).permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
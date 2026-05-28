package com.example.plogrid.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.plogrid.global.security.jwt.JwtAuthenticationFilter;
import com.example.plogrid.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String[] SECURITY_ALLOW_ARRAY = {
		"/api/v1/members/auth/sign-up",
		"/api/v1/members/auth/sign-in",
		"/api/v1/members/auth/reissue",
		"/api/v1/common/health/**",
		"/swagger-ui/**",
		"/swagger-resources/**",
		"/v3/api-docs/**",
		"/webjars/**",
		"/swagger-ui.html",
		"/favicon.ico"
	};

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(requests -> requests
				.requestMatchers(SECURITY_ALLOW_ARRAY).permitAll()
				.anyRequest().authenticated()
			)
			.csrf(csrf -> csrf.disable())
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class
			);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

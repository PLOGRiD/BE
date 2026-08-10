package com.example.plogrid.global.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
		"/api/v1/devices/token",
		"/api/v1/devices/reissue",
		"/api/v1/plogging/waste-classification",
		"/api/v1/ploggings/*/events",
		"/api/v1/health",
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
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class
			);
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

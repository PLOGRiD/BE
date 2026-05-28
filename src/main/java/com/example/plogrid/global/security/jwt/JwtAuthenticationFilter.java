package com.example.plogrid.global.security.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {

		if (isPermitAllPath(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = jwtTokenProvider.resolveToken(request);

		if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token) && !jwtTokenProvider.isBlacklisted(token)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private boolean isPermitAllPath(String path) {
		return path.startsWith("/swagger-ui")
			|| path.startsWith("/v3/api-docs")
			|| path.startsWith("/swagger-resources")
			|| path.startsWith("/webjars")
			|| path.equals("/swagger-ui.html")
			|| path.startsWith("/api/v1/members/auth/sign-up")
			|| path.startsWith("/api/v1/members/auth/sign-in")
			|| path.startsWith("/api/v1/members/auth/reissue")
			|| path.startsWith("/api/v1/common/health");
	}
}

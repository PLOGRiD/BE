package com.seohaeng.backend.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPermitAllPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && !jwtTokenProvider.isBlacklisted(token) && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setJsonResponse(HttpServletResponse response, int statusCode, String code, String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = String.format("{\"isSuccess\": false, \"code\": \"%s\", \"message\": \"%s\"}", code,
                message);
        response.getWriter().write(jsonResponse);
    }

    private boolean isPermitAllPath(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/swagger-ui.html")

                // 로그인/토큰발급 등등
                || path.startsWith("/api/v1/users/auth/**")
                || path.startsWith("/test")
                || path.startsWith("/api/v1/common/health/**")

                // 필요하다면 다른 permitAll 경로들도 추가
                // ...
                ;
    }
}
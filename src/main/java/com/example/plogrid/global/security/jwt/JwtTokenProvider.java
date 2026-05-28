package com.seohaeng.backend.global.security.jwt;

import com.seohaeng.backend.domain.user.entity.LoginInfo;
import com.seohaeng.backend.domain.user.repository.LoginInfoRepository;
import com.seohaeng.backend.global.apiPayload.code.status.ErrorStatus;
import com.seohaeng.backend.global.apiPayload.exception.handler.UserHandler;
import com.seohaeng.backend.global.security.userDetails.LocalCustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final LoginInfoRepository loginInfoRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HEADER_STRING = "Authorization";
    private static final String HEADER_STRING_PREFIX = "Bearer ";

    @Value("${jwt.token.secretKey}")
    private String signingKey;

    @Value("${jwt.token.expiration.access}")
    private long accessTokenExpiration;

    @Value("${jwt.token.expiration.refresh}")
    private long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication, long validityMilliseconds) {
        String username = authentication.getName();

        LoginInfo loginUserLoginInfo = loginInfoRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new UserHandler(ErrorStatus.LOGIN_INFO_NOT_FOUND));

        com.seohaeng.backend.domain.user.entity.User loginUser = loginUserLoginInfo.getUser();

        Long UserId = loginUser.getId();

        return Jwts.builder()
                .setSubject(username)
                .claim("id",UserId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityMilliseconds))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createAccessToken(Authentication authentication) {
        return generateToken(authentication, accessTokenExpiration);
    }

    public String createRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshTokenExpiration);
    }

    // 토큰 추출
    public String extractToken (final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HEADER_STRING);

        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_STRING_PREFIX)) {
            return authorizationHeader.substring(7);
        }
        throw new UserHandler(ErrorStatus.INVALID_TOKEN);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isBlacklisted(String token) {
        String redisKey = "blacklist:accessToken:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();

        LoginInfo loginInfo = loginInfoRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        com.seohaeng.backend.domain.user.entity.User user = loginInfo.getUser();
        LocalCustomUserDetails principal = new LocalCustomUserDetails(user, loginInfo);

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                principal.getAuthorities()
        );
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer " .length());
        }
        return null;
    }

    public Authentication extractAuthentication(HttpServletRequest request){
        String accessToken = resolveToken(request);
        if(accessToken == null || !validateToken(accessToken)) {
            throw new UserHandler(ErrorStatus.INVALID_TOKEN);
        }
        return getAuthentication(accessToken);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("id", Long.class);
    }

    public long getRemainingValidity(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();

            long remainingTime = expirationTime - currentTime;
            return Math.max(remainingTime, 0);
        } catch (JwtException | IllegalArgumentException e) {
            return 0;
        }
    }
}
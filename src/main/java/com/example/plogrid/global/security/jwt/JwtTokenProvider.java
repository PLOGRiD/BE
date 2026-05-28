package com.example.plogrid.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.plogrid.global.security.userdetails.MemberDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String BLACKLIST_PREFIX = "blacklist:accessToken:";
	private static final String REFRESH_PREFIX = "refresh:";

	private final StringRedisTemplate redisTemplate;

	@Value("${jwt.token.secretKey}")
	private String signingKey;

	@Value("${jwt.token.expiration.access}")
	private long accessTokenExpiration;

	@Value("${jwt.token.expiration.refresh}")
	private long refreshTokenExpiration;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
	}

	// ── Access Token ──────────────────────────────────────────────

	public String createAccessToken(Long memberId, String username) {
		return buildToken(memberId, username, accessTokenExpiration);
	}

	public void blacklistToken(String token) {
		long remainingMs = getRemainingValidity(token);
		if (remainingMs > 0) {
			redisTemplate.opsForValue().set(
				BLACKLIST_PREFIX + token,
				"blacklisted",
				remainingMs,
				TimeUnit.MILLISECONDS
			);
		}
	}

	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
	}

	// ── Refresh Token ─────────────────────────────────────────────

	public String createRefreshToken(Long memberId, String username) {
		return buildToken(memberId, username, refreshTokenExpiration);
	}

	public void saveRefreshToken(Long memberId, String refreshToken) {
		redisTemplate.opsForValue().set(
			REFRESH_PREFIX + memberId,
			refreshToken,
			refreshTokenExpiration,
			TimeUnit.MILLISECONDS
		);
	}

	public String getRefreshToken(Long memberId) {
		return redisTemplate.opsForValue().get(REFRESH_PREFIX + memberId);
	}

	public void deleteRefreshToken(Long memberId) {
		redisTemplate.delete(REFRESH_PREFIX + memberId);
	}

	// ── 공통 ──────────────────────────────────────────────────────

	private String buildToken(Long memberId, String username, long validityMs) {
		return Jwts.builder()
			.subject(username)
			.claim("id", memberId)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + validityMs))
			.signWith(getSigningKey())
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();

		MemberDetails principal = new MemberDetails(
			claims.get("id", Long.class),
			claims.getSubject(),
			null
		);
		return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
	}

	public Long getMemberIdFromToken(String token) {
		return Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("id", Long.class);
	}

	public long getRemainingValidity(String token) {
		try {
			Date expiration = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration();
			return Math.max(expiration.getTime() - System.currentTimeMillis(), 0);
		} catch (JwtException | IllegalArgumentException e) {
			return 0;
		}
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return null;
	}
}

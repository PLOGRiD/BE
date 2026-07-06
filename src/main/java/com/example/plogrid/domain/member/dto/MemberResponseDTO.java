package com.example.plogrid.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class MemberResponseDTO {

	@Schema(name = "토큰 응답")
	@Builder
	@Getter
	public static class Token {

		@Schema(description = "Access Token (유효기간 1시간)", example = "eyJhbGciOiJIUzI1NiJ9...")
		private String accessToken;

		@Schema(description = "Refresh Token (유효기간 14일)", example = "eyJhbGciOiJIUzI1NiJ9...")
		private String refreshToken;
	}

	@Schema(name = "사용자 정보 조회 응답")
	@Builder
	@Getter
	public static class MemberProfileDTO {

		@Schema(description = "사용자 닉네임", example = "김나나")
		private String nickName;

		@Schema(description = "이메일", example = "plogrid2026@gmail.com")
		private String email;
	}
}
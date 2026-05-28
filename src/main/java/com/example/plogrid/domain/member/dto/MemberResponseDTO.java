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
}

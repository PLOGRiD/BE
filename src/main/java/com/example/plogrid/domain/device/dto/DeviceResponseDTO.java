package com.example.plogrid.domain.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class DeviceResponseDTO {

	@Schema(name = "디바이스 토큰 응답")
	@Builder
	@Getter
	public static class TokenDTO {
		@Schema(description = "디바이스 Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
		private String deviceToken;

		@Schema(description = "디바이스 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
		private String refreshToken;
	}
}
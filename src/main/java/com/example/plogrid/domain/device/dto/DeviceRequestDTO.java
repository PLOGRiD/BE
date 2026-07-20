package com.example.plogrid.domain.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeviceRequestDTO {

	@Schema(name = "디바이스 토큰 요청")
	@Getter
	@NoArgsConstructor
	public static class TokenRequest {

		@Schema(description = "수거 디바이스 ID", example = "12")
		@NotNull(message = "값을 입력해주세요.")
		private Long deviceId;

		@Schema(description = "수거 디바이스 시리얼 번호", example = "PLG-DEVICE-0001")
		@NotNull(message = "값을 입력해주세요.")
		private String secretKey;
	}

	@Schema(name = "디바이스 토큰 재발급 요청")
	@Getter
	@NoArgsConstructor
	public static class Reissue {

		@Schema(description = "토큰 발급 시 받은 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
		@NotNull(message = "값을 입력해주세요.")
		private String refreshToken;
	}
}
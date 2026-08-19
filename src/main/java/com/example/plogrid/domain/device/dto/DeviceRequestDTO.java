package com.example.plogrid.domain.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeviceRequestDTO {

	@Schema(name = "디바이스 연동 요청")
	@Getter
	@NoArgsConstructor
	public static class LinkRequest {

		@Schema(description = "수거 디바이스 ID", example = "12")
		@NotNull(message = "값을 입력해주세요.")
		private Long deviceId;
	}
}

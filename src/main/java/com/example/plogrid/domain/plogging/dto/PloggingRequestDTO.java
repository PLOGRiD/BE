package com.example.plogrid.domain.plogging.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PloggingRequestDTO {

	@Schema(name = "플로깅 위치 갱신 요청")
	@Getter
	@Setter
	@NoArgsConstructor
	public static class UpdateLocationRequestDTO {

		@Schema(description = "위도", example = "37.5665")
		@NotNull(message = "위도를 입력해주세요.")
		private Double latitude;

		@Schema(description = "경도", example = "126.9780")
		@NotNull(message = "경도를 입력해주세요.")
		private Double longitude;
	}
}

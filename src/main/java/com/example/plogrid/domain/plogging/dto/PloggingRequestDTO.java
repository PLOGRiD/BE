package com.example.plogrid.domain.plogging.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PloggingRequestDTO {

	@Schema(name = "쓰레기 분석 요청")
	@Getter
	@Setter
	@NoArgsConstructor
	public static class WasteClassification {

		@Schema(description = "쓰레기 이미지 파일")
		@NotNull(message = "이미지를 입력해주세요.")
		private MultipartFile image;

		@Schema(description = "촬영 시각", example = "2026-07-12T15:30:00")
		@NotNull(message = "촬영 시각을 입력해주세요.")
		private LocalDateTime timestamp;

		@Schema(description = "위도", example = "37.5665")
		@NotNull(message = "위도를 입력해주세요.")
		private Double latitude;

		@Schema(description = "경도", example = "126.9780")
		@NotNull(message = "경도를 입력해주세요.")
		private Double longitude;
	}
}
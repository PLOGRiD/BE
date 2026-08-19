package com.example.plogrid.domain.trash.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TrashRequestDTO {

	@Schema(name = "쓰레기 분석 요청")
	@Getter
	@Setter
	@NoArgsConstructor
	public static class WasteClassification {

		@Schema(description = "디바이스 ID")
		@NotNull(message = "디바이스 ID를 입력해주세요.")
		private Long deviceId;

		@Schema(description = "쓰레기 이미지 파일")
		@NotNull(message = "이미지를 입력해주세요.")
		private MultipartFile image;

		@Schema(description = "촬영 시각", example = "2026-07-12T15:30:00")
		// @NotNull(message = "촬영 시각을 입력해주세요.")
		private LocalDateTime timestamp;

		@Schema(description = "위도", example = "37.5665")
		@NotNull(message = "위도를 입력해주세요.")
		private Double latitude;

		@Schema(description = "경도", example = "126.9780")
		@NotNull(message = "경도를 입력해주세요.")
		private Double longitude;

		@Schema(description = "A 채널 (410nm, Index 0)", example = "0.0")
		@NotNull(message = "A 채널 값을 입력해주세요.")
		private Double a;

		@Schema(description = "B 채널 (435nm, Index 1)", example = "0.0")
		@NotNull(message = "B 채널 값을 입력해주세요.")
		private Double b;

		@Schema(description = "C 채널 (460nm, Index 2)", example = "0.0")
		@NotNull(message = "C 채널 값을 입력해주세요.")
		private Double c;

		@Schema(description = "D 채널 (485nm, Index 3)", example = "0.0")
		@NotNull(message = "D 채널 값을 입력해주세요.")
		private Double d;

		@Schema(description = "E 채널 (510nm, Index 4)", example = "0.0")
		@NotNull(message = "E 채널 값을 입력해주세요.")
		private Double e;

		@Schema(description = "F 채널 (535nm, Index 5)", example = "0.0")
		@NotNull(message = "F 채널 값을 입력해주세요.")
		private Double f;

		@Schema(description = "G 채널 (560nm, Index 6)", example = "0.0")
		@NotNull(message = "G 채널 값을 입력해주세요.")
		private Double g;

		@Schema(description = "H 채널 (585nm, Index 7)", example = "0.0")
		@NotNull(message = "H 채널 값을 입력해주세요.")
		private Double h;

		@Schema(description = "I 채널 (610nm, Index 8)", example = "0.0")
		@NotNull(message = "I 채널 값을 입력해주세요.")
		private Double i;

		@Schema(description = "J 채널 (645nm, Index 9)", example = "0.0")
		@NotNull(message = "J 채널 값을 입력해주세요.")
		private Double j;

		@Schema(description = "K 채널 (680nm, Index 10)", example = "0.0")
		@NotNull(message = "K 채널 값을 입력해주세요.")
		private Double k;

		@Schema(description = "L 채널 (705nm, Index 11)", example = "0.0")
		@NotNull(message = "L 채널 값을 입력해주세요.")
		private Double l;

		@Schema(description = "R 채널 (730nm, Index 12)", example = "0.0")
		@NotNull(message = "R 채널 값을 입력해주세요.")
		private Double r;

		@Schema(description = "S 채널 (760nm, Index 13)", example = "0.0")
		@NotNull(message = "S 채널 값을 입력해주세요.")
		private Double s;

		@Schema(description = "T 채널 (810nm, Index 14)", example = "0.0")
		@NotNull(message = "T 채널 값을 입력해주세요.")
		private Double t;

		@Schema(description = "U 채널 (860nm, Index 15)", example = "0.0")
		@NotNull(message = "U 채널 값을 입력해주세요.")
		private Double u;

		@Schema(description = "V 채널 (900nm, Index 16)", example = "0.0")
		@NotNull(message = "V 채널 값을 입력해주세요.")
		private Double v;

		@Schema(description = "W 채널 (940nm, Index 17)", example = "0.0")
		@NotNull(message = "W 채널 값을 입력해주세요.")
		private Double w;
	}
}

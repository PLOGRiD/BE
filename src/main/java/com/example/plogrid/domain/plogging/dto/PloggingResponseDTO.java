package com.example.plogrid.domain.plogging.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class PloggingResponseDTO {

	@Schema(name = "쓰레기 분류 응답")
	@Builder
	@Getter
	public static class TrashClassificationResponseDTO {

		private YoloResponseDTO result;
		private SpectralResponseDTO spectralResult;
	}
}

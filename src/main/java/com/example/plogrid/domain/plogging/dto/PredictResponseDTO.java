package com.example.plogrid.domain.plogging.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "쓰레기 통합 분석 결과")
@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PredictResponseDTO {

	private String finalLabel;
	private boolean materialVerified;
	private boolean labelOverridden;
}

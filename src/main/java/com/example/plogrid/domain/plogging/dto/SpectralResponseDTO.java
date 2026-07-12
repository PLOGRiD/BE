package com.example.plogrid.domain.plogging.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "분광센서 분석 결과")
@Getter
@NoArgsConstructor
public class SpectralResponseDTO {

	private String label;
	private double probability;
	private Map<String, Double> scores;
}

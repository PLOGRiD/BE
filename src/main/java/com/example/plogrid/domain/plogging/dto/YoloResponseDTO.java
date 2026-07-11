package com.example.plogrid.domain.plogging.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "YOLO 분석 결과")
@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class YoloResponseDTO {

	private String filename;
	private ImageSize imageSize;
	private int numDetections;
	private List<Detection> detections;
	private double timestamp;

	@Getter
	@NoArgsConstructor
	public static class ImageSize {
		private int width;
		private int height;
	}

	@Getter
	@NoArgsConstructor
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class Detection {
		private int classId;
		private String className;
		private double confidence;
		private BoundingBox bbox;
	}

	@Getter
	@NoArgsConstructor
	public static class BoundingBox {
		private double x1;
		private double y1;
		private double x2;
		private double y2;
	}
}

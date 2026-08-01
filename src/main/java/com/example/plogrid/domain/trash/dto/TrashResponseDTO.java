package com.example.plogrid.domain.trash.dto;

import java.time.LocalDateTime;

import com.example.plogrid.domain.trash.entity.enums.TrashCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class TrashResponseDTO {

	@Schema(name = "쓰레기 지도 마커 응답")
	@Builder
	@Getter
	public static class TrashMapResponseDTO {
		private Long id;
		private double latitude;
		private double longitude;
		private TrashCategory category;
		private LocalDateTime collectedAt;
		private String imageUrl;
	}
}
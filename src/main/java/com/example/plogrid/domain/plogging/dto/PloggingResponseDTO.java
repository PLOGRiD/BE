package com.example.plogrid.domain.plogging.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.plogrid.domain.trash.entity.enums.TrashCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class PloggingResponseDTO {

	@Schema(name = "플로깅 시작 응답")
	@Builder
	@Getter
	public static class StartResponseDTO {

		private Long ploggingId;
	}

	@Schema(name = "쓰레기 분류 응답")
	@Builder
	@Getter
	public static class TrashClassificationResponseDTO {

		private YoloResponseDTO result;
		private SpectralResponseDTO spectralResult;
	}

	@Schema(name = "플로깅 종료 응답")
	@Builder
	@Getter
	public static class EndResponseDTO {

		private Long ploggingId;
		private double distanceMeters;
		private double durationSeconds;
		private int contributionScore;
		private TrashSummaryResponseDTO trashSummary;
		private List<TrashResponseDTO> trashes;
	}

	@Schema(name = "수거한 쓰레기 응답")
	@Builder
	@Getter
	public static class TrashSummaryResponseDTO {

		private int totalCount;
		private double vinylPercentage;
		private double paperPercentage;
		private double glassPercentage;
		private double canPercentage;
		private double petBottlePercentage;
		private double plasticPercentage;
		private double cigarettePercentage;
		private double styrofoamPercentage;
	}

	@Schema(name = "수거한 쓰레기 개별 상세 응답")
	@Builder
	@Getter
	public static class TrashResponseDTO {

		private Long trashId;
		private TrashCategory category;
		private String imageUrl;
	}

	@Schema(name = "가장 최근 플로깅 기록 응답")
	@Builder
	@Getter
	public static class RecentResponseDTO {

		private Long ploggingId;
		private double distanceMeters;
		private double durationSeconds;
		private int trashCount;
		private LocalDateTime createdAt;
	}

	@Schema(name = "플로깅 진행 중 응답")
	@Builder
	@Getter
	public static class PloggingProcessResponseDTO {
		private TrashCountSummaryResponseDTO trashSummary;
		private List<TrashLocationResponseDTO> trashLocations;
	}

	@Schema(name = "수거한 쓰레기 개수 요약 응답")
	@Builder
	@Getter
	public static class TrashCountSummaryResponseDTO {

		private int totalCount;
		private int vinylCount;
		private int paperCount;
		private int glassCount;
		private int canCount;
		private int petBottleCount;
		private int plasticCount;
		private int cigaretteCount;
		private int styrofoamCount;
	}

	@Schema(name = "쓰레기 위치 응답")
	@Builder
	@Getter
	public static class TrashLocationResponseDTO {
		private Long trashId;
		private double latitude;
		private double longitude;
	}

	@Schema(name = "쓰레기 추가 감지 응답")
	@Builder
	@Getter
	public static class TrashAddedResponseDTO {
		private Long trashId;
		private TrashCategory category;
		private double latitude;
		private double longitude;
	}

	@Schema(name = "플로깅 이동 거리 갱신 응답")
	@Builder
	@Getter
	public static class DistanceUpdatedResponseDTO {
		private double distanceMeters;
	}
}

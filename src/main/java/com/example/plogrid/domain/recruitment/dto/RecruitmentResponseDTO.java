package com.example.plogrid.domain.recruitment.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.plogrid.domain.recruitment.entity.enums.RecruitmentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class RecruitmentResponseDTO {

	@Schema(name = "모집글 목록 응답")
	@Builder
	@Getter
	public static class RecruitmentListResponseDTO {
		private Integer listSize;
		private Integer totalPage;
		private Long totalElements;
		private Boolean isFirst;
		private Boolean isLast;
		private List<RecruitmentInfoResponseDTO> recruitments;
	}

	@Schema(name = "모집글 개별 응답")
	@Builder
	@Getter
	public static class RecruitmentInfoResponseDTO {
		private String title;
		private String hostName;
		private LocalDateTime eventDateTime;
		private String eventLocation;
		private Integer currentParticipants;
		private Integer maxParticipants;
		private RecruitmentStatus eventStatus;
	}
}

package com.example.plogrid.domain.recruitment.converter;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.plogrid.domain.recruitment.dto.RecruitmentResponseDTO;
import com.example.plogrid.domain.recruitment.entity.Recruitment;

public class RecruitmentConverter {

	private RecruitmentConverter() {
	}

	public static RecruitmentResponseDTO.RecruitmentListResponseDTO toRecruitmentListResponseDTO(
		Page<Recruitment> recruitments, Map<Long, Integer> currentParticipantCounts) {
		return RecruitmentResponseDTO.RecruitmentListResponseDTO.builder()
			.listSize(recruitments.getNumberOfElements())
			.totalPage(recruitments.getTotalPages())
			.totalElements(recruitments.getTotalElements())
			.isFirst(recruitments.isFirst())
			.isLast(recruitments.isLast())
			.recruitments(recruitments.getContent().stream()
				.map(recruitment -> toRecruitmentInfoResponseDTO(recruitment,
					currentParticipantCounts.getOrDefault(recruitment.getId(), 0)))
				.toList())
			.build();
	}

	public static RecruitmentResponseDTO.RecruitmentInfoResponseDTO toRecruitmentInfoResponseDTO(
		Recruitment recruitment, int currentParticipants) {
		return RecruitmentResponseDTO.RecruitmentInfoResponseDTO.builder()
			.recruitmentId(recruitment.getId())
			.title(recruitment.getTitle())
			.hostName(recruitment.getHost().getName())
			.thumbnailImageUrl(recruitment.getThumbnailImageUrl())
			.eventDateTime(recruitment.getEventDateTime())
			.eventLocation(recruitment.getEventLocation())
			.currentParticipants(currentParticipants)
			.maxParticipants(recruitment.getMaxParticipants())
			.eventStatus(recruitment.getStatus())
			.build();
	}

	public static RecruitmentResponseDTO.RecruitmentDetailResponseDTO toRecruitmentDetailResponseDTO(
		Recruitment recruitment, int currentParticipants, boolean isParticipating) {
		return RecruitmentResponseDTO.RecruitmentDetailResponseDTO.builder()
			.title(recruitment.getTitle())
			.hostName(recruitment.getHost().getName())
			.thumbnailImageUrl(recruitment.getThumbnailImageUrl())
			.description(recruitment.getDescription())
			.eventDateTime(recruitment.getEventDateTime())
			.eventLocation(recruitment.getEventLocation())
			.currentParticipants(currentParticipants)
			.maxParticipants(recruitment.getMaxParticipants())
			.eventStatus(recruitment.getStatus())
			.isParticipating(isParticipating)
			.build();
	}

	public static RecruitmentResponseDTO.ParticipationToggleResponseDTO toParticipationToggleResponseDTO(
		boolean isParticipating, int currentParticipants, int maxParticipants) {
		return RecruitmentResponseDTO.ParticipationToggleResponseDTO.builder()
			.isParticipating(isParticipating)
			.currentParticipants(currentParticipants)
			.maxParticipants(maxParticipants)
			.build();
	}
}

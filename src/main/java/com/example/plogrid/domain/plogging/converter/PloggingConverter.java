package com.example.plogrid.domain.plogging.converter;

import java.util.List;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.trash.entity.Trash;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;

public class PloggingConverter {

	private PloggingConverter() {
	}

	public static PloggingResponseDTO.EndResponseDTO toEndResponseDTO(
		Plogging plogging, double distanceMeters, double durationSeconds, int contributionScore, List<Trash> trashes) {

		return PloggingResponseDTO.EndResponseDTO.builder()
			.ploggingId(plogging.getId())
			.distanceMeters(distanceMeters)
			.durationSeconds(durationSeconds)
			.contributionScore(contributionScore)
			.trashSummary(toTrashSummaryResponseDTO(trashes))
			.trashes(toTrashResponseDTOList(trashes))
			.build();
	}

	private static PloggingResponseDTO.TrashSummaryResponseDTO toTrashSummaryResponseDTO(List<Trash> trashes) {
		int totalCount = trashes.size();

		return PloggingResponseDTO.TrashSummaryResponseDTO.builder()
			.totalCount(totalCount)
			.vinylPercentage(percentage(trashes, TrashCategory.VINYL, totalCount))
			.paperPercentage(percentage(trashes, TrashCategory.PAPER, totalCount))
			.glassPercentage(percentage(trashes, TrashCategory.GLASS, totalCount))
			.canPercentage(percentage(trashes, TrashCategory.CAN, totalCount))
			.petBottlePercentage(percentage(trashes, TrashCategory.PET_BOTTLE, totalCount))
			.plasticPercentage(percentage(trashes, TrashCategory.PLASTIC, totalCount))
			.cigarettePercentage(percentage(trashes, TrashCategory.CIGARETTE, totalCount))
			.build();
	}

	private static List<PloggingResponseDTO.TrashResponseDTO> toTrashResponseDTOList(List<Trash> trashes) {
		return trashes.stream()
			.map(trash -> PloggingResponseDTO.TrashResponseDTO.builder()
				.trashId(trash.getId())
				.category(trash.getCategory())
				.imageUrl(trash.getTrashImage())
				.build())
			.toList();
	}

	private static double percentage(List<Trash> trashes, TrashCategory category, int totalCount) {
		if (totalCount == 0) {
			return 0;
		}

		long count = trashes.stream()
			.filter(trash -> trash.getCategory() == category)
			.count();

		return (double) count / totalCount * 100;
	}
}

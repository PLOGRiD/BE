package com.example.plogrid.domain.trash.converter;

import java.util.List;

import com.example.plogrid.domain.trash.dto.TrashResponseDTO;
import com.example.plogrid.domain.trash.entity.Trash;

public class TrashConverter {

	private TrashConverter() {
	}

	public static List<TrashResponseDTO.TrashMapResponseDTO> toTrashMapResponseDTOList(List<Trash> trashes) {
		return trashes.stream()
			.map(TrashConverter::toTrashMapResponseDTO)
			.toList();
	}

	public static TrashResponseDTO.TrashMapResponseDTO toTrashMapResponseDTO(Trash trash) {
		return TrashResponseDTO.TrashMapResponseDTO.builder()
			.id(trash.getId())
			.latitude(trash.getLatitude())
			.longitude(trash.getLongitude())
			.category(trash.getCategory())
			.collectedAt(trash.getCreatedAt())
			.imageUrl(trash.getTrashImage())
			.build();
	}
}

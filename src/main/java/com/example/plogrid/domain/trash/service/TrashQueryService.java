package com.example.plogrid.domain.trash.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.trash.converter.TrashConverter;
import com.example.plogrid.domain.trash.dto.TrashResponseDTO;
import com.example.plogrid.domain.trash.entity.Trash;
import com.example.plogrid.domain.trash.repository.TrashRepository;
import com.example.plogrid.global.apiPayload.code.TrashErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrashQueryService {

	private final TrashRepository trashRepository;

	@Value("${trash.viewport.max-result-size}")
	private int maxViewportResultSize;

	public List<TrashResponseDTO.TrashMapResponseDTO> getTrashesInViewport(double minLat, double maxLat, double minLng, double maxLng) {
		if (minLat > maxLat || minLng > maxLng) {
			throw new GeneralException(TrashErrorCode.INVALID_VIEWPORT);
		}

		List<Trash> trashes = trashRepository.findWithinViewport(minLat, maxLat, minLng, maxLng, maxViewportResultSize);
		return TrashConverter.toTrashMapResponseDTOList(trashes);
	}

	public TrashResponseDTO.TrashMapDetailResponseDTO getTrashDetail(Long trashId) {
		Trash trash = trashRepository.findById(trashId)
			.orElseThrow(() -> new GeneralException(TrashErrorCode.TRASH_NOT_FOUND));
		return TrashConverter.toTrashMapDetailResponseDTO(trash);
	}
}

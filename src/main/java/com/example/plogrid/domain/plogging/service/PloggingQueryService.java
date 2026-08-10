package com.example.plogrid.domain.plogging.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.plogging.converter.PloggingConverter;
import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.plogging.repository.PloggingRepository;
import com.example.plogrid.domain.trash.entity.Trash;
import com.example.plogrid.domain.trash.repository.TrashRepository;
import com.example.plogrid.global.apiPayload.code.PloggingErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PloggingQueryService {

	private final PloggingRepository ploggingRepository;
	private final TrashRepository trashRepository;

	public PloggingResponseDTO.RecentResponseDTO getRecentPlogging(Long memberId) {
		Plogging plogging = ploggingRepository
			.findFirstByMemberIdAndStatusOrderByCreatedAtDesc(memberId, PloggingStatus.COMPLETED)
			.orElseThrow(() -> new GeneralException(PloggingErrorCode.PLOGGING_NOT_FOUND));

		int trashCount = trashRepository.countByPloggingId(plogging.getId());

		return PloggingConverter.toRecentResponseDTO(plogging, trashCount);
	}

	public PloggingResponseDTO.PloggingProcessResponseDTO getPloggingProcess(Long ploggingId) {
		Plogging plogging = ploggingRepository.findById(ploggingId)
			.orElseThrow(() -> new GeneralException(PloggingErrorCode.PLOGGING_NOT_FOUND));

		List<Trash> trashes = trashRepository.findByPloggingId(ploggingId);

		return PloggingConverter.toPloggingProcessResponseDTO(trashes);
	}

	public Long getMemberId(Long ploggingId) {
		Plogging plogging = ploggingRepository.findById(ploggingId)
			.orElseThrow(() -> new GeneralException(PloggingErrorCode.PLOGGING_NOT_FOUND));

		return plogging.getMember().getId();
	}
}

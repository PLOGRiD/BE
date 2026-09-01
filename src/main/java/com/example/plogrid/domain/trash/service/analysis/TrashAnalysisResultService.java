package com.example.plogrid.domain.trash.service.analysis;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.repository.PloggingRepository;
import com.example.plogrid.domain.trash.entity.Trash;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;
import com.example.plogrid.domain.trash.event.TrashDetectedEvent;
import com.example.plogrid.domain.trash.repository.TrashRepository;
import com.example.plogrid.global.apiPayload.code.PloggingErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrashAnalysisResultService {

	private final PloggingRepository ploggingRepository;
	private final TrashRepository trashRepository;
	private final ApplicationEventPublisher eventPublisher;

	public void save(String sourceRecordId, Map<String, String> fields) {
		if (!"success".equals(fields.get("status"))) {
			log.warn("쓰레기 분석 실패 결과 수신 - fields: {}", fields);
			return;
		}

		if (trashRepository.existsBySourceRecordId(sourceRecordId)) {
			log.warn("이미 처리된 분석 결과 재전달, 스킵 - recordId: {}", sourceRecordId);
			return;
		}

		Long ploggingId = Long.valueOf(fields.get("ploggingId"));
		Plogging plogging = ploggingRepository.findById(ploggingId)
			.orElseThrow(() -> new GeneralException(PloggingErrorCode.PLOGGING_NOT_FOUND));

		String finalLabel = fields.get("finalLabel");
		boolean labelOverridden = Boolean.parseBoolean(fields.get("labelOverridden"));

		TrashCategory category;
		TrashSubCategory subCategory;
		if (labelOverridden) {
			category = TrashLabelResolver.resolveCategoryByMaterial(finalLabel);
			subCategory = null;
		} else {
			subCategory = TrashLabelResolver.resolveSubCategory(finalLabel);
			category = subCategory.getCategory();
		}

		Trash trash = Trash.create(
			plogging,
			sourceRecordId,
			fields.get("imageUrl"),
			Double.parseDouble(fields.get("latitude")),
			Double.parseDouble(fields.get("longitude")),
			category,
			subCategory
		);

		trashRepository.save(trash);

		Long memberId = plogging.getMember().getId();
		eventPublisher.publishEvent(new TrashDetectedEvent(
			memberId, trash.getId(), trash.getCategory(), trash.getLatitude(), trash.getLongitude()
		));
	}
}

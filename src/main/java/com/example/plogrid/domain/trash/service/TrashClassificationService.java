package com.example.plogrid.domain.trash.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.plogging.dto.PredictResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;
import com.example.plogrid.domain.trash.entity.Trash;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;
import com.example.plogrid.domain.trash.event.TrashDetectedEvent;
import com.example.plogrid.domain.trash.repository.TrashRepository;
import com.example.plogrid.global.apiPayload.code.TrashErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.s3.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TrashClassificationService {

	private final WastePredictionService wastePredictionService;
	private final TrashRepository trashRepository;
	private final S3Uploader s3Uploader;
	private final ApplicationEventPublisher eventPublisher;

	@Async("taskExecutor")
	public void trashClassification(
		TrashRequestDTO.WasteClassification request, Plogging plogging) throws IOException {

		PredictResponseDTO result = wastePredictionService.predict(request);

		TrashCategory category;
		TrashSubCategory subCategory;

		if (result.isLabelOverridden()) {
			category = resolveCategoryByMaterial(result.getFinalLabel());
			subCategory = null;
		} else {
			subCategory = resolveSubCategory(result.getFinalLabel());
			category = subCategory.getCategory();
		}

		String trashImage = s3Uploader.uploadTrashImage(request.getImage());

		Trash trash = Trash.create(
			plogging,
			trashImage,
			request.getLatitude(),
			request.getLongitude(),
			category,
			subCategory
		);

		trashRepository.save(trash);

		Long memberId = plogging.getMember().getId();

		eventPublisher.publishEvent(new TrashDetectedEvent(
			memberId, trash.getId(), trash.getCategory(), trash.getLatitude(), trash.getLongitude()
		));
	}

	private TrashSubCategory resolveSubCategory(String className) {
		return Arrays.stream(TrashSubCategory.values())
			.filter(subCategory -> subCategory.getName().equals(className))
			.findFirst()
			.orElseThrow(() -> new GeneralException(TrashErrorCode.UNKNOWN_TRASH_CLASS));
	}

	private TrashCategory resolveCategoryByMaterial(String materialLabel) {
		TrashCategory category = MATERIAL_LABEL_TO_CATEGORY.get(materialLabel);
		if (category == null) {
			throw new GeneralException(TrashErrorCode.UNKNOWN_TRASH_CLASS);
		}
		return category;
	}

	private static final Map<String, TrashCategory> MATERIAL_LABEL_TO_CATEGORY = Map.of(
		"유리", TrashCategory.GLASS,
		"투명 플라스틱", TrashCategory.PET_BOTTLE
	);
}

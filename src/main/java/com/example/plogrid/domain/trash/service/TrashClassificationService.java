package com.example.plogrid.domain.trash.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.plogging.dto.SpectralResponseDTO;
import com.example.plogrid.domain.plogging.dto.YoloResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.service.SpectralSensorService;
import com.example.plogrid.domain.plogging.service.YoloService;
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

	private final YoloService yoloService;
	private final SpectralSensorService spectralSensorService;
	private final TrashRepository trashRepository;
	private final S3Uploader s3Uploader;
	private final ApplicationEventPublisher eventPublisher;

	public void trashClassification(
		TrashRequestDTO.WasteClassification request, Plogging plogging) throws IOException {

		YoloResponseDTO result = yoloService.predict(request.getImage());

		YoloResponseDTO.Detection detection = result.getDetections().stream()
			.max(Comparator.comparingDouble(YoloResponseDTO.Detection::getConfidence))
			.orElseThrow(() -> new GeneralException(TrashErrorCode.NO_TRASH_DETECTED));

		TrashSubCategory subCategory = resolveSubCategory(detection.getClassName());
		TrashCategory category = subCategory.getCategory();

		boolean needsSpectralAnalysis = SPECTRAL_TARGET_CLASSES.contains(detection.getClassName());

		if (needsSpectralAnalysis) {
			SpectralResponseDTO spectralResult = spectralSensorService.predict(request);
			category = resolveCategory(spectralResult.getLabel());
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

	private TrashCategory resolveCategory(String label) {
		TrashCategory category = SPECTRAL_LABEL_TO_CATEGORY.get(label);
		if (category == null) {
			throw new GeneralException(TrashErrorCode.UNKNOWN_TRASH_CLASS);
		}
		return category;
	}

	private static final Map<String, TrashCategory> SPECTRAL_LABEL_TO_CATEGORY = Map.of(
		"glass", TrashCategory.GLASS,
		"PET", TrashCategory.PET_BOTTLE
	);

	private static final Set<String> SPECTRAL_TARGET_CLASSES = Set.of(
		TrashSubCategory.OTHER_BOTTLE.getName(),
		TrashSubCategory.BEER_BOTTLE.getName(),
		TrashSubCategory.TONIC_BOTTLE.getName(),
		TrashSubCategory.SOJU_BOTTLE.getName(),
		TrashSubCategory.BEVERAGE_BOTTLE.getName(),
		TrashSubCategory.KITCHEN_CONTAINER.getName(),
		TrashSubCategory.DISPOSABLE_DRINK_CUP.getName(),
		TrashSubCategory.PET_BOTTLE.getName()
	);
}

package com.example.plogrid.domain.trash.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
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
import com.example.plogrid.domain.trash.repository.TrashRepository;
import com.example.plogrid.global.apiPayload.code.TrashErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.s3.S3Uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrashClassificationService {

	private final YoloService yoloService;
	private final SpectralSensorService spectralSensorService;
	private final TrashRepository trashRepository;
	private final S3Uploader s3Uploader;

	@Async("taskExecutor")
	public void trashClassification(
		TrashRequestDTO.WasteClassification request, Plogging plogging) throws IOException {

		log.info("Trash classification started for plogging id {}", plogging.getId());

		YoloResponseDTO result = yoloService.predict(request.getImage());
		log.info("YOLO prediction completed: filename={}, {} detection(s)", result.getFilename(), result.getDetections().size());
		result.getDetections().forEach(d ->
			log.info("  detection: classId={}, className={}, confidence={}, bbox={}",
				d.getClassId(), d.getClassName(), d.getConfidence(), d.getBbox()));

		YoloResponseDTO.Detection detection = result.getDetections().stream()
			.max(Comparator.comparingDouble(YoloResponseDTO.Detection::getConfidence))
			.orElseThrow(() -> new GeneralException(TrashErrorCode.NO_TRASH_DETECTED));
		log.info("Selected detection: className={}, confidence={}", detection.getClassName(), detection.getConfidence());

		TrashSubCategory subCategory = resolveSubCategory(detection.getClassName());
		TrashCategory category = subCategory.getCategory();
		log.info("Resolved subCategory={}, category={}", subCategory, category);

		boolean needsSpectralAnalysis = SPECTRAL_TARGET_CLASSES.contains(detection.getClassName());
		log.info("Needs spectral analysis: {}", needsSpectralAnalysis);

		if (needsSpectralAnalysis) {
			SpectralResponseDTO spectralResult = spectralSensorService.predict(request);
			log.info("Spectral analysis raw result: label={}, probability={}, scores={}",
				spectralResult.getLabel(), spectralResult.getProbability(), spectralResult.getScores());
			category = resolveCategory(spectralResult.getLabel());
			log.info("Spectral analysis result: label={}, resolved category={}", spectralResult.getLabel(), category);
		}

		String trashImage = s3Uploader.uploadTrashImage(request.getImage());
		log.info("Trash image uploaded: {}", trashImage);

		Trash trash = Trash.create(
			plogging,
			trashImage,
			request.getLatitude(),
			request.getLongitude(),
			category,
			subCategory
		);

		trashRepository.save(trash);
		log.info("Trash saved: id={}, plogging id={}", trash.getId(), plogging.getId());
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

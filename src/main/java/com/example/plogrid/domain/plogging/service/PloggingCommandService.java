package com.example.plogrid.domain.plogging.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.plogging.dto.PloggingRequestDTO;
import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.dto.SpectralResponseDTO;
import com.example.plogrid.domain.plogging.dto.YoloResponseDTO;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PloggingCommandService {

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

	private final YoloService yoloService;
	private final SpectralSensorService spectralSensorService;

	public PloggingResponseDTO.TrashClassificationResponseDTO trashClassification(
		PloggingRequestDTO.WasteClassification request) throws IOException {

		YoloResponseDTO result = yoloService.predict(request.getImage());

		SpectralResponseDTO spectralResult = null;
		boolean needsSpectralAnalysis = result.getDetections().stream()
			.anyMatch(detection -> SPECTRAL_TARGET_CLASSES.contains(detection.getClassName()));

		if (needsSpectralAnalysis) {
			spectralResult = spectralSensorService.predict(request);
		}

		return PloggingResponseDTO.TrashClassificationResponseDTO.builder()
			.result(result)
			.spectralResult(spectralResult)
			.build();
	}
}

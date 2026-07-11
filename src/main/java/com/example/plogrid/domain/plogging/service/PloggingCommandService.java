package com.example.plogrid.domain.plogging.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.plogging.dto.PloggingRequestDTO;
import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.dto.YoloResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PloggingCommandService {

	private final YoloService yoloService;

	public PloggingResponseDTO.TrashClassificationResponseDTO trashClassification(
		PloggingRequestDTO.WasteClassification request) throws IOException {

		YoloResponseDTO result = yoloService.predict(request.getImage());

		return PloggingResponseDTO.TrashClassificationResponseDTO.builder()
			.result(result)
			.build();
	}
}

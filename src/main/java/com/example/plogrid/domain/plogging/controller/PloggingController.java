package com.example.plogrid.domain.plogging.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.plogging.dto.PloggingRequestDTO;
import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.service.PloggingCommandService;
import com.example.plogrid.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Plogging", description = "플로깅 API")
@RestController
@RequestMapping("/api/v1/plogging")
@RequiredArgsConstructor
public class PloggingController {

	private final PloggingCommandService ploggingCommandService;

	@Operation(
		summary = "쓰레기 분석 API",
		description = """
			이미지를 YOLO 모델에 전달해 쓰레기 종류를 분석합니다.

			"""
	)
	@PostMapping(value = "/waste-classification", consumes = "multipart/form-data")
	public ApiResponse<PloggingResponseDTO.TrashClassificationResponseDTO> trashClassification(
		@Valid @ModelAttribute PloggingRequestDTO.WasteClassification request) throws IOException {
		return ApiResponse.onSuccess(ploggingCommandService.trashClassification(request));
	}
}
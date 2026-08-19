package com.example.plogrid.domain.trash.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;
import com.example.plogrid.domain.trash.dto.TrashResponseDTO;
import com.example.plogrid.domain.trash.service.TrashCommandService;
import com.example.plogrid.domain.trash.service.TrashQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;

@Tag(name = "Trash", description = "쓰레기 API")
@RestController
@RequestMapping("/api/v1/trashes")
@RequiredArgsConstructor
@Validated
public class TrashController {

	private final TrashQueryService trashQueryService;
	private final TrashCommandService trashCommandService;

	@Operation(
		summary = "지도 뷰포트 내 쓰레기 목록 조회 API",
		description = """
        뷰포트에 맞추어 지도 위 쓰레기 투기 현황을 조회합니다.
        Parameter:
        - `minLat`: 뷰포트 남쪽 경계 위도
        - `maxLat`: 뷰포트 북쪽 경계 위도
        - `minLng`: 뷰포트 서쪽 경계 경도
        - `maxLng`: 뷰포트 동쪽 경계 경도
    """
	)
	@GetMapping
	public ApiResponse<List<TrashResponseDTO.TrashMapResponseDTO>> getTrashesInViewport(
		@RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double minLat,
		@RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double maxLat,
		@RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double minLng,
		@RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double maxLng
	) {
		return ApiResponse.onSuccess(trashQueryService.getTrashesInViewport(minLat, maxLat, minLng, maxLng));
	}

	@Operation(
		summary = "쓰레기 상세 조회 API",
		description = """
        쓰레기 ID로 상세 정보를 조회합니다.
        Parameter:
        - `trashId`: 조회할 쓰레기 ID
    """
	)
	@GetMapping("/{trashId}")
	public ApiResponse<TrashResponseDTO.TrashMapDetailResponseDTO> getTrashDetail(
		@PathVariable Long trashId
	) {
		return ApiResponse.onSuccess(trashQueryService.getTrashDetail(trashId));
	}

	@Operation(
		summary = "[디바이스] 쓰레기 분석 API",
		description = """
        쓰레기 수거 장치가 전달한 이미지와 분광센서 값으로 쓰레기 종류를 분석하고 저장합니다.
        (임시 프로토타입: 디바이스 인증 없이 요청 바디의 `deviceId`로 디바이스를 식별합니다.)

        - `deviceId`에 연결된 회원의 진행 중인 플로깅 세션에만 등록됩니다. 디바이스가 연결되어 있지 않거나 진행 중인 플로깅이 없으면 실패합니다.
        - YOLO 모델로 이미지를 1차 탐지한 뒤, confidence가 가장 높은 탐지 결과를 기준으로 쓰레기 서브카테고리를 판별합니다.
        - 유리병/페트병처럼 육안(YOLO)만으로는 재질 구분이 어려운 항목은 분광센서 값으로 재질(대분류 카테고리)을 재판별합니다.
        - 이미지 분석·업로드·저장은 비동기로 처리되며, 해당 API는 처리 완료를 기다리지 않고 즉시 응답합니다.

        Parameter:
        - `deviceId`: 쓰레기 수거 장치 ID
        - `image`: 쓰레기 이미지 파일
        - `timestamp`: 촬영 시각
        - `latitude`, `longitude`: 촬영 위치 좌표
        - `a` ~ `w`: 분광센서 채널 값 (재질 판별이 필요한 항목에만 사용됨)
    """
	)
	@PostMapping(value = "/waste-classification", consumes = "multipart/form-data")
	public ApiResponse<PloggingResponseDTO.TrashClassificationResponseDTO> trashClassification(
		@Valid @ModelAttribute TrashRequestDTO.WasteClassification request) throws IOException {
		trashCommandService.trashProcess(request, request.getDeviceId());
		return ApiResponse.onSuccess(null);
	}
}
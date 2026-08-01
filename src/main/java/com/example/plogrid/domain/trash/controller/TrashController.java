package com.example.plogrid.domain.trash.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.trash.dto.TrashResponseDTO;
import com.example.plogrid.domain.trash.service.TrashQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;

@Tag(name = "Trash", description = "쓰레기 API")
@RestController
@RequestMapping("/api/v1/trash")
@RequiredArgsConstructor
@Validated
public class TrashController {

	private final TrashQueryService trashQueryService;

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
	@GetMapping("/map")
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
	public ApiResponse<TrashResponseDTO.TrashMapResponseDTO> getTrashDetail(
		@PathVariable Long trashId
	) {
		return ApiResponse.onSuccess(trashQueryService.getTrashDetail(trashId));
	}
}
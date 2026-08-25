package com.example.plogrid.domain.recruitment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.recruitment.dto.RecruitmentResponseDTO;
import com.example.plogrid.domain.recruitment.service.RecruitmentCommandService;
import com.example.plogrid.domain.recruitment.service.RecruitmentQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "Recruitment", description = "단체 플로깅 모집 API")
@RestController
@RequestMapping("/api/v1/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

	private final RecruitmentQueryService recruitmentQueryService;
	private final RecruitmentCommandService recruitmentCommandService;

	@Operation(
		summary = "모집글 목록 조회 API",
		description = """
        단체 플로깅 모집글 목록을 최신순으로 페이징 처리하여 조회합니다.
        Parameter:
        - `page`: 페이지 번호 (1부터 시작)
        - `size`: 한 페이지당 모집글 개수 (기본값: 10)
    """
	)
	@GetMapping
	public ApiResponse<RecruitmentResponseDTO.RecruitmentListResponseDTO> getRecruitmentList(
		@RequestParam(name = "page", defaultValue = "1") @Min(1) Integer page,
		@RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size
	) {
		return ApiResponse.onSuccess(recruitmentQueryService.getRecruitmentList(page, size));
	}

	@Operation(
		summary = "모집글 상세 조회 API",
		description = """
        모집글 ID로 단체 플로깅 모집글 상세 정보를 조회합니다.
        Parameter:
        - `recruitmentId`: 모집글 ID
    """
	)
	@GetMapping("/{recruitmentId}")
	public ApiResponse<RecruitmentResponseDTO.RecruitmentDetailResponseDTO> getRecruitmentDetail(
		@AuthUser Long memberId,
		@PathVariable Long recruitmentId
	) {
		return ApiResponse.onSuccess(recruitmentQueryService.getRecruitmentDetail(memberId, recruitmentId));
	}

	@Operation(
		summary = "모집글 참여 토글 API",
		description = """
        단체 플로깅 모집글에 대한 참여를 신청/취소합니다.
        이미 참여한 상태라면 취소되고, 참여하지 않은 상태라면 신청됩니다.
        Parameter:
        - `recruitmentId`: 모집글 ID
    """
	)
	@PostMapping("/{recruitmentId}/participation")
	public ApiResponse<RecruitmentResponseDTO.ParticipationToggleResponseDTO> toggleParticipation(
		@AuthUser Long memberId,
		@PathVariable Long recruitmentId
	) {
		return ApiResponse.onSuccess(recruitmentCommandService.toggleParticipation(memberId, recruitmentId));
	}

}

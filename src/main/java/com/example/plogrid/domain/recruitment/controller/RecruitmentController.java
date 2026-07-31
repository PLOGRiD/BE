package com.example.plogrid.domain.recruitment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.recruitment.dto.RecruitmentResponseDTO;
import com.example.plogrid.domain.recruitment.service.RecruitmentQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "Recruitment", description = "단체 플로깅 모집 API")
@RestController
@RequestMapping("/api/v1/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

	private final RecruitmentQueryService recruitmentQueryService;

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

}

package com.example.plogrid.domain.plogging.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.service.PloggingCommandService;
import com.example.plogrid.domain.plogging.service.PloggingQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Plogging", description = "플로깅 API")
@RestController
@RequestMapping("/api/v1/ploggings")
@RequiredArgsConstructor
public class PloggingController {

	private final PloggingCommandService ploggingCommandService;
	private final PloggingQueryService ploggingQueryService;

	@Operation(
		summary = "플로깅 시작 API",
		description = """
			현재 로그인한 회원의 플로깅 세션을 새로 생성합니다.
			
			- 회원에게 연동된 수거 디바이스가 없으면 401(DEVICE401_1, 연동되지 않은 디바이스입니다)로 거절됩니다.
			- 생성된 세션은 상태(status)가 IN_PROGRESS로 시작하며, 이동 거리/시간은 0으로 초기화됩니다.
			"""
	)
	@PostMapping
	public ApiResponse<PloggingResponseDTO.StartResponseDTO> startPlogging(@AuthUser Long memberId) {
		return ApiResponse.onSuccess(ploggingCommandService.startPlogging(memberId));
	}

	@Operation(
		summary = "플로깅 종료 API",
		description = """
			현재 로그인한 회원의 진행 중인 플로깅 세션을 종료합니다.

			- 진행 중인 플로깅이 없으면 404(PLOGGING404_1, 진행 중인 플로깅이 없습니다)로 거절됩니다.
			- 세션 상태를 COMPLETED로 변경하고, 연동되어 있던 수거 디바이스의 연동도 함께 해제합니다.
			"""
	)
	@PatchMapping
	public ApiResponse<PloggingResponseDTO.EndResponseDTO> endPlogging(@AuthUser Long memberId) {
		return ApiResponse.onSuccess(ploggingCommandService.endPlogging(memberId));
	}

	@Operation(
		summary = "가장 최근 플로깅 기록 조회 API",
		description = """
			현재 로그인한 회원의 가장 최근에 완료된 플로깅 기록을 조회합니다.

			- 완료된 플로깅 기록이 없으면 404(PLOGGING404_2, 플로깅 기록이 없습니다)로 거절됩니다.
			- 이동 거리(m), 진행 시간(초), 수거한 쓰레기 개수를 반환합니다.
			"""
	)
	@GetMapping("/recent")
	public ApiResponse<PloggingResponseDTO.RecentResponseDTO> getRecentPlogging(@AuthUser Long memberId) {
		return ApiResponse.onSuccess(ploggingQueryService.getRecentPlogging(memberId));
	}
}
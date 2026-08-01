package com.example.plogrid.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.member.dto.MemberResponseDTO;
import com.example.plogrid.domain.member.service.MemberQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberQueryService memberQueryService;

	@Operation(
		summary = "회원 정보 조회 API",
		description = """
			로그인한 회원의 정보를 조회합니다.

			- `Authorization: Bearer {accessToken}` 헤더가 필요합니다.
			"""
	)
	@GetMapping("/me")
	public ApiResponse<MemberResponseDTO.MemberProfileDTO> getMyProfile(@AuthUser Long memberId) {
		return ApiResponse.onSuccess(memberQueryService.getMyProfile(memberId));
	}

	@Operation(
		summary = "사용자 환경 기여 정보 조회 API",
		description = """
			로그인한 회원의 환경 기여 정보를 조회합니다.

			- `Authorization: Bearer {accessToken}` 헤더가 필요합니다.
			"""
	)
	@GetMapping("/contribution")
	public ApiResponse<MemberResponseDTO.MemberContributionDTO> getMyContribution(@AuthUser Long memberId) {
		return ApiResponse.onSuccess(memberQueryService.getMyContribution(memberId));
	}

	@Operation(
		summary = "환경 기여도 랭킹 조회 API",
		description = """
			환경 기여도 점수 기준 상위 10위 랭킹과 로그인한 회원의 랭킹을 조회합니다.

			- `Authorization: Bearer {accessToken}` 헤더가 필요합니다.
			"""
	)
	@GetMapping("/ranking")
	public ApiResponse<MemberResponseDTO.MemberRankingResultDTO> getRanking(@AuthUser Long memberId) {
		return ApiResponse.onSuccess(memberQueryService.getRanking(memberId));
	}
}
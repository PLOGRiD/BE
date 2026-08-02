package com.example.plogrid.domain.member.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.member.dto.MemberResponseDTO;
import com.example.plogrid.domain.member.service.MemberAuthService;
import com.example.plogrid.domain.member.service.MemberQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AccessToken;
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
	private final MemberAuthService memberAuthService;

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
	@GetMapping("/me/contribution")
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

	@Operation(
		summary = "회원 탈퇴 API",
		description = """
			회원 탈퇴를 처리합니다.

			- 탈퇴 후 아이디/이메일은 익명화되어 동일 정보로 재가입이 가능합니다.
			- 사용자 닉네임은 '알 수 없음'으로 표시되며, 플로깅 기록 등의 환경 데이터는 보존됩니다.
			- `Authorization: Bearer {accessToken}` 헤더가 필요합니다.
			"""
	)
	@DeleteMapping("/me")
	public ApiResponse<Void> withdraw(@AuthUser Long memberId, @AccessToken String accessToken) {
		memberAuthService.withdraw(memberId, accessToken);
		return ApiResponse.onSuccess(null);
	}
}

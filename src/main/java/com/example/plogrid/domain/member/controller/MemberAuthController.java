package com.example.plogrid.domain.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.member.dto.MemberRequestDTO;
import com.example.plogrid.domain.member.dto.MemberResponseDTO;
import com.example.plogrid.domain.member.service.MemberAuthService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AccessToken;
import com.example.plogrid.global.security.handler.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "회원 인증 API")
@RestController
@RequestMapping("/api/v1/members/auth")
@RequiredArgsConstructor
public class MemberAuthController {

	private final MemberAuthService memberAuthService;

	@Operation(
		summary = "회원가입 API",
		description = """
			아이디, 비밀번호, 비밀번호 확인, 이메일로 회원가입합니다.

			- 아이디: 띄어쓰기 없이 영문/숫자 6~10자
			- 비밀번호: 8~15자, 영문 + 숫자 또는 특수문자 조합 필수
			- 비밀번호 확인이 비밀번호와 일치해야 합니다.
			- 닉네임은 아이디와 동일하게 자동 설정됩니다.
			"""
	)
	@PostMapping("/sign-up")
	public ApiResponse<Void> signUp(@RequestBody @Valid MemberRequestDTO.SignUp request) {
		memberAuthService.signUp(request);
		return ApiResponse.onSuccess(null);
	}

	@Operation(
		summary = "로그인 API",
		description = """
			아이디와 비밀번호로 로그인합니다.

			- 응답으로 `accessToken`(유효기간 1시간)과 `refreshToken`(유효기간 14일)을 반환합니다.
			- 이후 인증이 필요한 API 요청 시 `Authorization: Bearer {accessToken}` 헤더를 포함해야 합니다.
			"""
	)
	@PostMapping("/sign-in")
	public ApiResponse<MemberResponseDTO.Token> signIn(@RequestBody @Valid MemberRequestDTO.SignIn request) {
		return ApiResponse.onSuccess(memberAuthService.signIn(request));
	}

	@Operation(
		summary = "액세스 토큰 재발급 API",
		description = """
			Refresh Token으로 새로운 Access Token과 Refresh Token을 재발급합니다.

			- 재발급 시 기존 Refresh Token은 즉시 무효화됩니다 (토큰 로테이션).
			- Access Token 만료 시 이 API를 호출해 토큰을 갱신하세요.
			"""
	)
	@PostMapping("/reissue")
	public ApiResponse<MemberResponseDTO.Token> reissue(@RequestBody @Valid MemberRequestDTO.Reissue request) {
		return ApiResponse.onSuccess(memberAuthService.reissue(request));
	}

	@Operation(
		summary = "로그아웃 API",
		description = """
			현재 로그인된 회원을 로그아웃합니다.

			- Refresh Token이 즉시 삭제됩니다.
			- Access Token이 블랙리스트에 등록되어 즉시 사용 불가 상태가 됩니다.
			- `Authorization: Bearer {accessToken}` 헤더가 필요합니다.
			"""
	)
	@PostMapping("/sign-out")
	public ApiResponse<Void> signOut(@AuthUser Long memberId, @AccessToken String accessToken) {
		memberAuthService.signOut(memberId, accessToken);
		return ApiResponse.onSuccess(null);
	}
}

package com.example.plogrid.domain.member.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class MemberResponseDTO {

	@Schema(name = "토큰 응답")
	@Builder
	@Getter
	public static class Token {

		@Schema(description = "Access Token (유효기간 1시간)", example = "eyJhbGciOiJIUzI1NiJ9...")
		private String accessToken;

		@Schema(description = "Refresh Token (유효기간 14일)", example = "eyJhbGciOiJIUzI1NiJ9...")
		private String refreshToken;
	}

	@Schema(name = "사용자 정보 조회 응답")
	@Builder
	@Getter
	public static class MemberProfileDTO {

		@Schema(description = "사용자 ID", example = "1")
		private Long memberId;

		@Schema(description = "사용자 닉네임", example = "김나나")
		private String nickName;

		@Schema(description = "이메일", example = "plogrid2026@gmail.com")
		private String email;
	}

	@Schema(name = "사용자 환경 기여 정보 조회 응답")
	@Builder
	@Getter
	public static class MemberContributionDTO {

		@Schema(description = "플로깅 횟수", example = "12")
		private int ploggingCount;

		@Schema(description = "총 이동 거리 (미터)", example = "15230.5")
		private double totalDistanceMeters;

		@Schema(description = "총 진행 시간 (초)", example = "18320.0")
		private double totalDurationSeconds;

		@Schema(description = "총 쓰레기 수거량 (개)", example = "48")
		private int totalTrashCount;

		@Schema(description = "환경 기여 점수", example = "320")
		private int contributionScore;

		@Schema(description = "수거 쓰레기 유형")
		private MemberTrashCategoryDTO trashCategory;
	}

	@Schema(name = "랭킹 항목")
	@Builder
	@Getter
	public static class MemberRankingDTO {

		@Schema(description = "순위", example = "1")
		private int rank;

		@Schema(description = "사용자 ID", example = "1")
		private Long memberId;

		@Schema(description = "사용자 닉네임", example = "김나나")
		private String nickName;

		@Schema(description = "환경 기여 점수", example = "320")
		private int contributionScore;
	}

	@Schema(name = "환경 기여도 랭킹 조회 응답")
	@Builder
	@Getter
	public static class MemberRankingResultDTO {

		@Schema(description = "상위 10위 랭킹 목록")
		private List<MemberRankingDTO> topRankings;

		@Schema(description = "로그인한 회원의 랭킹")
		private MemberRankingDTO myRanking;
	}

	@Schema(name = "카테고리별 쓰레기 수거량 조회 응답")
	@Builder
	@Getter
	public static class MemberTrashCategoryDTO {

		@Schema(description = "비닐 수거량 (개)", example = "5")
		private int vinylCount;

		@Schema(description = "유리 수거량 (개)", example = "3")
		private int glassCount;

		@Schema(description = "종이 수거량 (개)", example = "8")
		private int paperCount;

		@Schema(description = "캔 수거량 (개)", example = "6")
		private int canCount;

		@Schema(description = "페트 수거량 (개)", example = "10")
		private int petCount;

		@Schema(description = "플라스틱 수거량 (개)", example = "9")
		private int plasticCount;

		@Schema(description = "담배꽁초 수거량 (개)", example = "4")
		private int cigaretteButtCount;

		@Schema(description = "기타 수거량 (개)", example = "3")
		private int etcCount;
	}
}
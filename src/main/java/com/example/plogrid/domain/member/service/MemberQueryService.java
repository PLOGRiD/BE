package com.example.plogrid.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.member.dto.MemberResponseDTO;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.entity.MemberStatistics;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.domain.member.repository.MemberStatisticsRepository;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

	private final MemberRepository memberRepository;
	private final MemberStatisticsRepository memberStatisticsRepository;

	public MemberResponseDTO.MemberProfileDTO getMyProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

		return MemberResponseDTO.MemberProfileDTO.builder()
			.nickName(member.getNickname())
			.email(member.getEmail())
			.build();
	}

	public MemberResponseDTO.MemberContributionDTO getMyContribution(Long memberId) {
		MemberStatistics statistics = memberStatisticsRepository.findByMemberId(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.STATISTICS_NOT_FOUND));

		MemberResponseDTO.MemberTrashCategoryDTO trashCategory = MemberResponseDTO.MemberTrashCategoryDTO.builder()
			.vinylCount(statistics.getVinylCount())
			.glassCount(statistics.getGlassCount())
			.paperCount(statistics.getPaperCount())
			.canCount(statistics.getCanCount())
			.petCount(statistics.getPetCount())
			.plasticCount(statistics.getPlasticCount())
			.cigaretteButtCount(statistics.getCigaretteButtCount())
			.etcCount(statistics.getEtcCount())
			.build();

		return MemberResponseDTO.MemberContributionDTO.builder()
			.ploggingCount(statistics.getPloggingCount())
			.totalDistanceMeters(statistics.getTotalDistanceMeters())
			.totalTrashCount(statistics.getTotalTrashCount())
			.contributionScore(statistics.getContributionScore())
			.trashCategory(trashCategory)
			.build();
	}
}

package com.example.plogrid.domain.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
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
			.memberId(member.getId())
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
			.totalDurationSeconds(statistics.getTotalDurationSeconds())
			.totalTrashCount(statistics.getTotalTrashCount())
			.contributionScore(statistics.getContributionScore())
			.trashCategory(trashCategory)
			.build();
	}

	public MemberResponseDTO.MemberRankingResultDTO getRanking(Long memberId) {
		List<MemberStatistics> topStatistics = memberStatisticsRepository
			.findRankingsOrderByContributionScoreDesc(PageRequest.of(0, 10));

		List<MemberResponseDTO.MemberRankingDTO> topRankings = new ArrayList<>();
		int rank = 1;
		for (MemberStatistics statistics : topStatistics) {
			topRankings.add(toRankingDTO(rank++, statistics));
		}

		MemberStatistics myStatistics = memberStatisticsRepository.findByMemberId(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.STATISTICS_NOT_FOUND));

		int myRank = (int) memberStatisticsRepository
			.countByContributionScoreGreaterThan(myStatistics.getContributionScore()) + 1;

		return MemberResponseDTO.MemberRankingResultDTO.builder()
			.topRankings(topRankings)
			.myRanking(toRankingDTO(myRank, myStatistics))
			.build();
	}

	private MemberResponseDTO.MemberRankingDTO toRankingDTO(int rank, MemberStatistics statistics) {
		return MemberResponseDTO.MemberRankingDTO.builder()
			.rank(rank)
			.memberId(statistics.getMember().getId())
			.nickName(statistics.getMember().getNickname())
			.contributionScore(statistics.getContributionScore())
			.build();
	}
}

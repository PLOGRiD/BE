package com.example.plogrid.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.member.entity.MemberStatistics;
import com.example.plogrid.domain.member.repository.MemberStatisticsRepository;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberStatisticsService {

	private final MemberStatisticsRepository memberStatisticsRepository;

	public void reflectPloggingResult(
		Long memberId, double distanceMeters, double durationSeconds, List<TrashCategory> categories, int contributionScore) {
		MemberStatistics statistics = memberStatisticsRepository.findByMemberId(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

		statistics.reflectPlogging(distanceMeters, durationSeconds, categories, contributionScore);
	}
}

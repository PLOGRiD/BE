package com.example.plogrid.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.plogrid.domain.member.entity.MemberStatistics;

public interface MemberStatisticsRepository extends JpaRepository<MemberStatistics, Long> {

	Optional<MemberStatistics> findByMemberId(Long memberId);

	@Query("SELECT ms FROM MemberStatistics ms JOIN FETCH ms.member ORDER BY ms.contributionScore DESC")
	List<MemberStatistics> findRankingsOrderByContributionScoreDesc(Pageable pageable);

	long countByContributionScoreGreaterThan(int contributionScore);
}

package com.example.plogrid.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.member.entity.MemberStatistics;

public interface MemberStatisticsRepository extends JpaRepository<MemberStatistics, Long> {

	Optional<MemberStatistics> findByMemberId(Long memberId);
}

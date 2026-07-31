package com.example.plogrid.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.recruitment.entity.RecruitmentParticipant;

public interface RecruitmentParticipantRepository extends JpaRepository<RecruitmentParticipant, Long> {

	int countByRecruitmentId(Long recruitmentId);
}

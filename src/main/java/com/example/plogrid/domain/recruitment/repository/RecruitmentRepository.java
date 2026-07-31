package com.example.plogrid.domain.recruitment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.plogrid.domain.recruitment.entity.Recruitment;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

	Page<Recruitment> findAllByOrderByCreatedAtDesc(Pageable pageable);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Recruitment r SET r.currentParticipants = r.currentParticipants + 1 "
		+ "WHERE r.id = :recruitmentId AND r.currentParticipants < r.maxParticipants")
	int increaseCurrentParticipants(@Param("recruitmentId") Long recruitmentId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Recruitment r SET r.currentParticipants = r.currentParticipants - 1 "
		+ "WHERE r.id = :recruitmentId AND r.currentParticipants > 0")
	int decreaseCurrentParticipants(@Param("recruitmentId") Long recruitmentId);
}

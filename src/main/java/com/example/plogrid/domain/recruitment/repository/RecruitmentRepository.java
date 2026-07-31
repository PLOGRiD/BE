package com.example.plogrid.domain.recruitment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.recruitment.entity.Recruitment;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

	Page<Recruitment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

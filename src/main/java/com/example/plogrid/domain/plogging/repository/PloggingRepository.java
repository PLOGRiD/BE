package com.example.plogrid.domain.plogging.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;

public interface PloggingRepository extends JpaRepository<Plogging, Long> {

	Optional<Plogging> findByMemberIdAndStatus(Long memberId, PloggingStatus status);

	Optional<Plogging> findFirstByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, PloggingStatus status);
}

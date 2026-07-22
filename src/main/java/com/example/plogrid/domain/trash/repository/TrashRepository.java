package com.example.plogrid.domain.trash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.trash.entity.Trash;

public interface TrashRepository extends JpaRepository<Trash, Long> {

	List<Trash> findByPloggingId(Long ploggingId);
}

package com.example.plogrid.domain.trash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.plogrid.domain.trash.entity.Trash;

public interface TrashRepository extends JpaRepository<Trash, Long> {

	List<Trash> findByPloggingId(Long ploggingId);

	int countByPloggingId(Long ploggingId);

	@Query(value = """
		SELECT * FROM trash t
		WHERE t.location && ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326)
		ORDER BY t.created_at DESC
		LIMIT :limit
		""", nativeQuery = true)
	List<Trash> findWithinViewport(
		@Param("minLat") double minLat,
		@Param("maxLat") double maxLat,
		@Param("minLng") double minLng,
		@Param("maxLng") double maxLng,
		@Param("limit") int limit
	);
}
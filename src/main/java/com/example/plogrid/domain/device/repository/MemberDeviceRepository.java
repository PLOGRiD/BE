package com.example.plogrid.domain.device.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.plogrid.domain.device.entity.MemberCollectionDevice;

public interface MemberDeviceRepository extends JpaRepository<MemberCollectionDevice, Long> {

	@Query("select mcd from MemberCollectionDevice mcd "
		+ "join fetch mcd.collectionDevice "
		+ "where mcd.collectionDevice.id = :collectionDeviceId")
	Optional<MemberCollectionDevice> findByCollectionDeviceId(@Param("collectionDeviceId") Long collectionDeviceId);

	boolean existsByCollectionDeviceId(Long collectionDeviceId);

	boolean existsByMemberId(Long memberId);

	void deleteByMemberId(Long memberId);
}

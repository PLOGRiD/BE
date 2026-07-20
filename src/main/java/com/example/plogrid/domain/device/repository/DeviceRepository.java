package com.example.plogrid.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.device.entity.CollectionDevice;

public interface DeviceRepository extends JpaRepository<CollectionDevice, Long> {

}

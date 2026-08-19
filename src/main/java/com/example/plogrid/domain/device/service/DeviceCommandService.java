package com.example.plogrid.domain.device.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.device.dto.DeviceRequestDTO;
import com.example.plogrid.domain.device.entity.CollectionDevice;
import com.example.plogrid.domain.device.entity.MemberCollectionDevice;
import com.example.plogrid.domain.device.repository.DeviceRepository;
import com.example.plogrid.domain.device.repository.MemberDeviceRepository;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.global.apiPayload.code.DeviceErrorCode;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceCommandService {

	private final MemberDeviceRepository memberDeviceRepository;
	private final DeviceRepository deviceRepository;
	private final MemberRepository memberRepository;

	public void linkDevice(Long memberId, DeviceRequestDTO.LinkRequest request) {
		CollectionDevice collectionDevice = deviceRepository.findById(request.getDeviceId())
			.orElseThrow(() -> new GeneralException(DeviceErrorCode.DEVICE_NOT_FOUND));

		if (memberDeviceRepository.existsByMemberId(memberId)) {
			throw new GeneralException(DeviceErrorCode.MEMBER_ALREADY_LINKED);
		}

		if (memberDeviceRepository.existsByCollectionDeviceId(collectionDevice.getId())) {
			throw new GeneralException(DeviceErrorCode.DEVICE_ALREADY_LINKED);
		}

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

		memberDeviceRepository.save(MemberCollectionDevice.create(member, collectionDevice));
	}
}
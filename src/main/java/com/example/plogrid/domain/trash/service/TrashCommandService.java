package com.example.plogrid.domain.trash.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.device.entity.MemberCollectionDevice;
import com.example.plogrid.domain.device.repository.MemberDeviceRepository;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;
import com.example.plogrid.global.apiPayload.code.DeviceErrorCode;
import com.example.plogrid.global.apiPayload.code.PloggingErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TrashCommandService {

	private final MemberDeviceRepository memberDeviceRepository;
	private final TrashClassificationService trashClassificationService;

	public void trashProcess(TrashRequestDTO.WasteClassification request, Long deviceId) throws IOException {
		MemberCollectionDevice memberCollectionDevice = memberDeviceRepository.findByCollectionDeviceId(deviceId)
			.orElseThrow(() -> new GeneralException(DeviceErrorCode.DEVICE_NOT_LINKED));

		Plogging plogging = memberCollectionDevice.getPlogging();

		if (!plogging.getStatus().equals(PloggingStatus.IN_PROGRESS)) {
			throw new GeneralException(PloggingErrorCode.PLOGGING_NOT_IN_PROGRESS);
		}

		trashClassificationService.trashClassification(request, plogging);
	}
}

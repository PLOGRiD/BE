package com.example.plogrid.domain.trash.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.device.entity.MemberCollectionDevice;
import com.example.plogrid.domain.device.repository.MemberDeviceRepository;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;
import com.example.plogrid.domain.trash.service.analysis.TrashAnalysisRequestProducer;
import com.example.plogrid.global.apiPayload.code.DeviceErrorCode;
import com.example.plogrid.global.apiPayload.code.PloggingErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.sse.SseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TrashCommandService {

	private final MemberDeviceRepository memberDeviceRepository;
	private final TrashAnalysisRequestProducer trashAnalysisRequestProducer;
	private final SseService sseService;

	public void trashProcessViaStream(TrashRequestDTO.WasteClassification request, Long deviceId) {
		MemberCollectionDevice memberCollectionDevice = memberDeviceRepository.findByCollectionDeviceId(deviceId)
			.orElseThrow(() -> new GeneralException(DeviceErrorCode.DEVICE_NOT_LINKED));

		Plogging plogging = memberCollectionDevice.getPlogging();

		if (!plogging.getStatus().equals(PloggingStatus.IN_PROGRESS)) {
			throw new GeneralException(PloggingErrorCode.PLOGGING_NOT_IN_PROGRESS);
		}

		Long memberId = memberCollectionDevice.getMember().getId();
		sseService.send(memberId, "trash-detective-event", "쓰레기 투입이 감지되었어요!");

		trashAnalysisRequestProducer.publish(request, plogging);
	}
}

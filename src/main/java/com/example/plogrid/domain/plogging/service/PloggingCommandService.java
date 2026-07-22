package com.example.plogrid.domain.plogging.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.device.repository.MemberDeviceRepository;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.domain.plogging.dto.PloggingRequestDTO;
import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.dto.SpectralResponseDTO;
import com.example.plogrid.domain.plogging.dto.YoloResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.plogging.repository.PloggingRepository;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;
import com.example.plogrid.global.apiPayload.code.DeviceErrorCode;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.code.PloggingErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PloggingCommandService {

	private final YoloService yoloService;
	private final SpectralSensorService spectralSensorService;
	private final PloggingRepository ploggingRepository;
	private final MemberRepository memberRepository;
	private final MemberDeviceRepository memberDeviceRepository;

	public PloggingResponseDTO.StartResponseDTO startPlogging(Long memberId) {
		if (!memberDeviceRepository.existsByMemberId(memberId)) {
			throw new GeneralException(DeviceErrorCode.DEVICE_NOT_LINKED);
		}

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

		Plogging plogging = ploggingRepository.save(Plogging.create(member));

		return PloggingResponseDTO.StartResponseDTO.builder()
			.ploggingId(plogging.getId())
			.build();
	}

	public PloggingResponseDTO.EndResponseDTO endPlogging(Long memberId) {
		Plogging plogging = ploggingRepository.findByMemberIdAndStatus(memberId, PloggingStatus.IN_PROGRESS)
			.orElseThrow(() -> new GeneralException(PloggingErrorCode.PLOGGING_NOT_IN_PROGRESS));

		plogging.complete();
		memberDeviceRepository.deleteByMemberId(memberId);

		return PloggingResponseDTO.EndResponseDTO.builder()
			.ploggingId(plogging.getId())
			.build();
	}

	public PloggingResponseDTO.TrashClassificationResponseDTO trashClassification(
		PloggingRequestDTO.WasteClassification request) throws IOException {

		YoloResponseDTO result = yoloService.predict(request.getImage());

		SpectralResponseDTO spectralResult = null;
		boolean needsSpectralAnalysis = result.getDetections().stream()
			.anyMatch(detection -> SPECTRAL_TARGET_CLASSES.contains(detection.getClassName()));

		if (needsSpectralAnalysis) {
			spectralResult = spectralSensorService.predict(request);
		}

		return PloggingResponseDTO.TrashClassificationResponseDTO.builder()
			.result(result)
			.spectralResult(spectralResult)
			.build();
	}

	private static final Set<String> SPECTRAL_TARGET_CLASSES = Set.of(
		TrashSubCategory.OTHER_BOTTLE.getName(),
		TrashSubCategory.BEER_BOTTLE.getName(),
		TrashSubCategory.TONIC_BOTTLE.getName(),
		TrashSubCategory.SOJU_BOTTLE.getName(),
		TrashSubCategory.BEVERAGE_BOTTLE.getName(),
		TrashSubCategory.KITCHEN_CONTAINER.getName(),
		TrashSubCategory.DISPOSABLE_DRINK_CUP.getName(),
		TrashSubCategory.PET_BOTTLE.getName()
	);
}

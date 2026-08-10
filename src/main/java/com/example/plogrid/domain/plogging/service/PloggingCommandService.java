package com.example.plogrid.domain.plogging.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.device.repository.MemberDeviceRepository;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.domain.member.service.MemberStatisticsService;
import com.example.plogrid.domain.plogging.converter.PloggingConverter;
import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.dto.SpectralResponseDTO;
import com.example.plogrid.domain.plogging.dto.YoloResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.plogging.repository.PloggingRepository;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;
import com.example.plogrid.domain.trash.entity.Trash;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;
import com.example.plogrid.domain.trash.repository.TrashRepository;
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
	private final TrashRepository trashRepository;
	private final MemberStatisticsService memberStatisticsService;

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

		memberDeviceRepository.deleteByMemberId(memberId);

		// TODO : 이동거리 계산 로직 미구현으로 임시 값 사용
		double distanceMeters = 1500.0;
		double durationSeconds = Duration.between(plogging.getCreatedAt(), LocalDateTime.now()).getSeconds();

		List<Trash> trashes = trashRepository.findByPloggingId(plogging.getId());
		int contributionScore = calculateContributionScore(trashes);

		List<TrashCategory> categories = trashes.stream()
			.map(Trash::getCategory)
			.toList();

		memberStatisticsService.reflectPloggingResult(memberId, distanceMeters, durationSeconds, categories, contributionScore);

		plogging.complete(distanceMeters, durationSeconds);

		return PloggingConverter.toEndResponseDTO(plogging, distanceMeters, durationSeconds, contributionScore, trashes);
	}

	private int calculateContributionScore(List<Trash> trashes) {
		return trashes.stream()
			.mapToInt(trash -> trash.getCategory().getContributionWeight() * 10)
			.sum();
	}

	public PloggingResponseDTO.TrashClassificationResponseDTO trashClassification(
		TrashRequestDTO.WasteClassification request) throws IOException {

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

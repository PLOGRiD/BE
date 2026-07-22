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
import com.example.plogrid.domain.plogging.dto.PloggingRequestDTO;
import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.dto.SpectralResponseDTO;
import com.example.plogrid.domain.plogging.dto.YoloResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.plogging.repository.PloggingRepository;
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
		int totalCount = trashes.size();

		PloggingResponseDTO.TrashSummaryResponseDTO trashSummary = PloggingResponseDTO.TrashSummaryResponseDTO.builder()
			.totalCount(totalCount)
			.vinylPercentage(percentage(trashes, TrashCategory.VINYL, totalCount))
			.paperPercentage(percentage(trashes, TrashCategory.PAPER, totalCount))
			.glassPercentage(percentage(trashes, TrashCategory.GLASS, totalCount))
			.canPercentage(percentage(trashes, TrashCategory.CAN, totalCount))
			.petBottlePercentage(percentage(trashes, TrashCategory.PET_BOTTLE, totalCount))
			.plasticPercentage(percentage(trashes, TrashCategory.PLASTIC, totalCount))
			.cigarettePercentage(percentage(trashes, TrashCategory.CIGARETTE, totalCount))
			.build();

		List<PloggingResponseDTO.TrashResponseDTO> trashResponses = trashes.stream()
			.map(trash -> PloggingResponseDTO.TrashResponseDTO.builder()
				.trashId(trash.getId())
				.category(trash.getCategory())
				.imageUrl(trash.getTrashImage())
				.build())
			.toList();

		int contributionScore = calculateContributionScore(trashes);

		List<TrashCategory> categories = trashes.stream()
			.map(Trash::getCategory)
			.toList();

		memberStatisticsService.reflectPloggingResult(memberId, distanceMeters, durationSeconds, categories, contributionScore);

		plogging.complete();

		return PloggingResponseDTO.EndResponseDTO.builder()
			.ploggingId(plogging.getId())
			.distanceMeters(distanceMeters)
			.durationSeconds(durationSeconds)
			.contributionScore(contributionScore)
			.trashSummary(trashSummary)
			.trashes(trashResponses)
			.build();
	}

	private int calculateContributionScore(List<Trash> trashes) {
		return trashes.stream()
			.mapToInt(trash -> trash.getCategory().getContributionWeight() * 10)
			.sum();
	}

	private double percentage(List<Trash> trashes, TrashCategory category, int totalCount) {
		if (totalCount == 0) {
			return 0;
		}

		long count = trashes.stream()
			.filter(trash -> trash.getCategory() == category)
			.count();

		return (double) count / totalCount * 100;
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

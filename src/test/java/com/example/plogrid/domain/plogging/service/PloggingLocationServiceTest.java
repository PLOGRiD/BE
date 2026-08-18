package com.example.plogrid.domain.plogging.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.plogging.repository.PloggingRepository;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.sse.SseService;

@ExtendWith(MockitoExtension.class)
class PloggingLocationServiceTest {

	private static final Long MEMBER_ID = 1L;
	private static final Long PLOGGING_ID = 100L;

	@Mock
	private PloggingRepository ploggingRepository;

	@Mock
	private SseService sseService;

	@Mock
	private Plogging plogging;

	private StringRedisTemplate redisTemplate;
	private PloggingLocationService ploggingLocationService;

	@BeforeEach
	void setUp() {
		RedisConnectionFactory connectionFactory = new LettuceConnectionFactory("localhost", 6379);
		((LettuceConnectionFactory) connectionFactory).afterPropertiesSet();
		redisTemplate = new StringRedisTemplate(connectionFactory);
		redisTemplate.afterPropertiesSet();

		ploggingLocationService = new PloggingLocationService(redisTemplate, ploggingRepository, sseService);
	}

	@AfterEach
	void tearDown() {
		ploggingLocationService.clear(PLOGGING_ID);
	}

	@Test
	void 진행중인_플로깅이_없으면_예외를_던지고_SSE는_보내지_않는다() {
		when(ploggingRepository.findByMemberIdAndStatus(MEMBER_ID, PloggingStatus.IN_PROGRESS))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> ploggingLocationService.updateLocation(MEMBER_ID, 37.5665, 126.9780))
			.isInstanceOf(GeneralException.class);

		verifyNoInteractions(sseService);
	}

	@Test
	void 위치가_갱신되면_누적_거리를_SSE로_전송한다() {
		when(ploggingRepository.findByMemberIdAndStatus(MEMBER_ID, PloggingStatus.IN_PROGRESS))
			.thenReturn(Optional.of(plogging));
		when(plogging.getId()).thenReturn(PLOGGING_ID);

		// 서울시청 -> 광화문 (직선 거리 약 1km)
		ploggingLocationService.updateLocation(MEMBER_ID, 37.5663, 126.9779);
		ploggingLocationService.updateLocation(MEMBER_ID, 37.5759, 126.9769);

		ArgumentCaptor<PloggingResponseDTO.DistanceUpdatedResponseDTO> payloadCaptor =
			ArgumentCaptor.forClass(PloggingResponseDTO.DistanceUpdatedResponseDTO.class);

		verify(sseService, times(2))
			.send(eq(MEMBER_ID), eq("plogging-distance-updated"), payloadCaptor.capture());

		double firstDistance = payloadCaptor.getAllValues().get(0).getDistanceMeters();
		double secondDistance = payloadCaptor.getAllValues().get(1).getDistanceMeters();

		assertThat(firstDistance).isEqualTo(0.0);
		assertThat(secondDistance).isBetween(900.0, 1200.0);
		assertThat(ploggingLocationService.getAccumulatedDistance(PLOGGING_ID)).isEqualTo(secondDistance);
	}

	@Test
	void clear_호출시_누적_거리가_초기화된다() {
		when(ploggingRepository.findByMemberIdAndStatus(MEMBER_ID, PloggingStatus.IN_PROGRESS))
			.thenReturn(Optional.of(plogging));
		when(plogging.getId()).thenReturn(PLOGGING_ID);

		ploggingLocationService.updateLocation(MEMBER_ID, 37.5663, 126.9779);
		ploggingLocationService.updateLocation(MEMBER_ID, 37.5759, 126.9769);
		assertThat(ploggingLocationService.getAccumulatedDistance(PLOGGING_ID)).isGreaterThan(0.0);

		ploggingLocationService.clear(PLOGGING_ID);

		assertThat(ploggingLocationService.getAccumulatedDistance(PLOGGING_ID)).isEqualTo(0.0);
	}
}

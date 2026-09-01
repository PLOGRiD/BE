package com.example.plogrid.domain.trash.service.analysis;

import java.time.Duration;
import java.util.List;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrashAnalysisResultPelReaper {

	private static final Duration MIN_IDLE = Duration.ofSeconds(40);
	private static final long MAX_DELIVERIES = 3;
	private static final long SCAN_COUNT = 100;

	private final StringRedisTemplate stringRedisTemplate;
	private final TrashAnalysisResultConsumer trashAnalysisResultConsumer;

	@Scheduled(fixedDelay = 30_000)
	public void reclaimAbandoned() {
		PendingMessages pendingMessages = stringRedisTemplate.opsForStream()
			.pending(TrashAnalysisResultConsumer.STREAM_KEY, TrashAnalysisResultConsumer.GROUP,
				Range.unbounded(), SCAN_COUNT);

		for (PendingMessage pendingMessage : pendingMessages) {
			if (pendingMessage.getElapsedTimeSinceLastDelivery().compareTo(MIN_IDLE) < 0) {
				continue;
			}

			try {
				reclaim(pendingMessage);
			} catch (Exception e) {
				log.error("PEL 회수 중 예상 밖 예외, 다음 메시지로 진행 - recordId: {}",
					pendingMessage.getId(), e);
			}
		}
	}

	private void reclaim(PendingMessage pendingMessage) {
		if (pendingMessage.getTotalDeliveryCount() > MAX_DELIVERIES) {
			log.error("{}회 재시도 후에도 실패, 포기하고 ACK 처리 - recordId: {}",
				pendingMessage.getTotalDeliveryCount(), pendingMessage.getId());
			stringRedisTemplate.opsForStream()
				.acknowledge(TrashAnalysisResultConsumer.STREAM_KEY, TrashAnalysisResultConsumer.GROUP,
					pendingMessage.getId());
			return;
		}

		List<MapRecord<String, String, String>> claimed = stringRedisTemplate.<String, String>opsForStream()
			.claim(TrashAnalysisResultConsumer.STREAM_KEY, TrashAnalysisResultConsumer.GROUP,
				trashAnalysisResultConsumer.getConsumerName(), MIN_IDLE, pendingMessage.getId());

		claimed.forEach(trashAnalysisResultConsumer::onMessage);
	}
}

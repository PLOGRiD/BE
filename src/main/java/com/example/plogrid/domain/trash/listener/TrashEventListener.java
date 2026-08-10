package com.example.plogrid.domain.trash.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.plogrid.domain.plogging.service.PloggingQueryService;
import com.example.plogrid.domain.trash.event.TrashDetectedEvent;
import com.example.plogrid.global.sse.SseService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrashEventListener {

	private final PloggingQueryService ploggingQueryService;
	private final SseService sseService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleTrashDetected(TrashDetectedEvent event) {
		sseService.send(
			event.memberId(),
			"plogging-in-progress",
			ploggingQueryService.getPloggingProcess(event.ploggingId())
		);
	}
}

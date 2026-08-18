package com.example.plogrid.domain.trash.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.trash.event.TrashDetectedEvent;
import com.example.plogrid.global.sse.SseService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrashEventListener {

	private final SseService sseService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleTrashDetected(TrashDetectedEvent event) {
		sseService.send(
			event.memberId(),
			"trash-added",
			PloggingResponseDTO.TrashAddedResponseDTO.builder()
				.trashId(event.trashId())
				.category(event.category())
				.latitude(event.latitude())
				.longitude(event.longitude())
				.build()
		);
	}
}

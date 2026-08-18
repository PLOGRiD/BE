package com.example.plogrid.domain.trash.listener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.domain.trash.event.TrashDetectedEvent;
import com.example.plogrid.global.sse.SseService;

@ExtendWith(MockitoExtension.class)
class TrashEventListenerTest {

	@Mock
	private SseService sseService;

	@InjectMocks
	private TrashEventListener trashEventListener;

	@Captor
	private ArgumentCaptor<Object> payloadCaptor;

	@Test
	void 트래시_감지_이벤트를_받으면_DB_재조회_없이_델타만_전송한다() {
		TrashDetectedEvent event = new TrashDetectedEvent(1L, 7L, TrashCategory.PLASTIC, 37.5673, 126.9779);

		trashEventListener.handleTrashDetected(event);

		verify(sseService).send(eq(1L), eq("trash-added"), payloadCaptor.capture());

		PloggingResponseDTO.TrashAddedResponseDTO payload =
			(PloggingResponseDTO.TrashAddedResponseDTO) payloadCaptor.getValue();

		org.assertj.core.api.Assertions.assertThat(payload.getTrashId()).isEqualTo(7L);
		org.assertj.core.api.Assertions.assertThat(payload.getCategory()).isEqualTo(TrashCategory.PLASTIC);
		org.assertj.core.api.Assertions.assertThat(payload.getLatitude()).isEqualTo(37.5673);
		org.assertj.core.api.Assertions.assertThat(payload.getLongitude()).isEqualTo(126.9779);
	}
}

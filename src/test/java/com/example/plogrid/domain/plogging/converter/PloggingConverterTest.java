package com.example.plogrid.domain.plogging.converter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.trash.entity.Trash;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;

class PloggingConverterTest {

	@Test
	void 진행중_응답의_쓰레기_요약은_퍼센트가_아니라_개수다() {
		Trash vinyl1 = mock(Trash.class);
		when(vinyl1.getId()).thenReturn(1L);
		when(vinyl1.getCategory()).thenReturn(TrashCategory.VINYL);
		when(vinyl1.getLatitude()).thenReturn(37.5665);
		when(vinyl1.getLongitude()).thenReturn(126.9780);

		Trash vinyl2 = mock(Trash.class);
		when(vinyl2.getId()).thenReturn(2L);
		when(vinyl2.getCategory()).thenReturn(TrashCategory.VINYL);
		when(vinyl2.getLatitude()).thenReturn(37.5666);
		when(vinyl2.getLongitude()).thenReturn(126.9781);

		Trash can = mock(Trash.class);
		when(can.getId()).thenReturn(3L);
		when(can.getCategory()).thenReturn(TrashCategory.CAN);
		when(can.getLatitude()).thenReturn(37.5667);
		when(can.getLongitude()).thenReturn(126.9782);

		PloggingResponseDTO.PloggingProcessResponseDTO result =
			PloggingConverter.toPloggingProcessResponseDTO(List.of(vinyl1, vinyl2, can));

		assertThat(result.getTrashSummary().getTotalCount()).isEqualTo(3);
		assertThat(result.getTrashSummary().getVinylCount()).isEqualTo(2);
		assertThat(result.getTrashSummary().getCanCount()).isEqualTo(1);
		assertThat(result.getTrashSummary().getGlassCount()).isEqualTo(0);
		assertThat(result.getTrashLocations()).hasSize(3);
	}

	@Test
	void 쓰레기가_없으면_모든_개수는_0이다() {
		PloggingResponseDTO.PloggingProcessResponseDTO result =
			PloggingConverter.toPloggingProcessResponseDTO(List.of());

		assertThat(result.getTrashSummary().getTotalCount()).isEqualTo(0);
		assertThat(result.getTrashSummary().getVinylCount()).isEqualTo(0);
		assertThat(result.getTrashLocations()).isEmpty();
	}
}

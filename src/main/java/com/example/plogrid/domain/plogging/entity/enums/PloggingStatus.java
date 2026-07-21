package com.example.plogrid.domain.plogging.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PloggingStatus {
	IN_PROGRESS("진행중"),
	COMPLETED("완료");

	private final String name;
}

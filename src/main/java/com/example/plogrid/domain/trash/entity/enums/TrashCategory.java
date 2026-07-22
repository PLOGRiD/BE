package com.example.plogrid.domain.trash.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrashCategory {
	PLASTIC("플라스틱", 3),
	GLASS("유리", 7),
	PAPER("종이", 1),
	CAN("캔", 3),
	STYROFOAM("스티로폼", 3),
	VINYL("비닐", 2),
	CIGARETTE("담배", 2),
	PET_BOTTLE("페트병", 3);

	private final String name;
	private final int contributionWeight;
	// contributionWeight
	// ceil(log10(자연 분해 기간(년) + 1))
}
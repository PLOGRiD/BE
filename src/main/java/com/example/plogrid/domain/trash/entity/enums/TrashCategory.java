package com.example.plogrid.domain.trash.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrashCategory {
	PLASTIC("플라스틱"),
	GLASS("유리"),
	PAPER("종이"),
	CAN("캔"),
	STYROFOAM("스티로폼"),
	VINYL("비닐"),
	CIGARETTE("담배"),
	PET_BOTTLE("페트병");

	private final String name;
}
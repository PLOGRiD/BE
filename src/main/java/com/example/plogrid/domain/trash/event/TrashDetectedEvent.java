package com.example.plogrid.domain.trash.event;

import com.example.plogrid.domain.trash.entity.enums.TrashCategory;

public record TrashDetectedEvent(
	Long memberId,
	Long trashId,
	TrashCategory category,
	double latitude,
	double longitude
) {
}

package com.example.plogrid.domain.recruitment.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitmentStatus {
	UPCOMING("이벤트예정"),
	ENDED("이벤트종료");

	private final String name;
}

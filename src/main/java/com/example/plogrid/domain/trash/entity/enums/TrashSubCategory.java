package com.example.plogrid.domain.trash.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrashSubCategory {

	CIGARETTE_BUTT("담배꽁초"),
	SNACK_BAG("과자봉지"),
	PLASTIC_BAG("봉투"),
	AIR_CAP("에어캡"),
	PACKAGING("포장재"),
	FOOD_TRAY("네모트레이"),
	PROTECTIVE_MATERIAL("보호재"),
	STYROFOAM("스티로폼"),
	FOOD_CONTAINER("포장용기"),
	OTHER_BOTTLE("기타술병"),
	BEER_BOTTLE("맥주병"),
	TONIC_BOTTLE("박카스병"),
	SOJU_BOTTLE("소주병"),
	BEVERAGE_BOTTLE("음료수병"),
	KITCHEN_CONTAINER("주방용기"),
	BOX("상자류"),
	BEVERAGE_CARTON("음료수곽"),
	PAPER_BAG("종이봉투"),
	BOOK("책자"),
	PACKAGING_BOX("포장상자"),
	BEER_CAN("맥주캔"),
	CAN("스팸류"),
	DRINK_CAN("음료수캔"),
	COFFEE_CAN("커피캔"),
	TIN_CAN("통조림캔"),
	DISPOSABLE_DRINK_CUP("일회용음료수잔"),
	PET_BOTTLE("페트병"),
	LARGE_PLASTIC_CONTAINER("대용량플라스틱통"),
	WASTE_CONTAINER("밀폐용기"),
	BATHROOM_SUPPLIES("욕실용품");

	private final String name;
}

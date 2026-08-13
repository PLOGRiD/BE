package com.example.plogrid.domain.trash.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrashSubCategory {

	CIGARETTE_BUTT("담배꽁초", TrashCategory.CIGARETTE),
	SNACK_BAG("과자봉지", TrashCategory.VINYL),
	PLASTIC_BAG("봉투", TrashCategory.VINYL),
	AIR_CAP("에어캡", TrashCategory.VINYL),
	PACKAGING("포장재", TrashCategory.VINYL),
	FOOD_TRAY("네모트레이", TrashCategory.STYROFOAM),
	PROTECTIVE_MATERIAL("보호재", TrashCategory.STYROFOAM),
	STYROFOAM("스티로폼", TrashCategory.STYROFOAM),
	FOOD_CONTAINER("포장용기", TrashCategory.STYROFOAM),
	OTHER_BOTTLE("기타술병", TrashCategory.GLASS),
	BEER_BOTTLE("맥주병", TrashCategory.GLASS),
	TONIC_BOTTLE("박카스병", TrashCategory.GLASS),
	SOJU_BOTTLE("소주병", TrashCategory.GLASS),
	BEVERAGE_BOTTLE("음료수병", TrashCategory.GLASS),
	KITCHEN_CONTAINER("주방용기", TrashCategory.GLASS),
	BOX("상자류", TrashCategory.PAPER),
	BEVERAGE_CARTON("음료수곽", TrashCategory.PAPER),
	PAPER_BAG("종이봉투", TrashCategory.PAPER),
	BOOK("책자", TrashCategory.PAPER),
	PACKAGING_BOX("포장상자", TrashCategory.PAPER),
	BEER_CAN("맥주캔", TrashCategory.CAN),
	CAN("스팸류", TrashCategory.CAN),
	DRINK_CAN("음료수캔", TrashCategory.CAN),
	COFFEE_CAN("커피캔", TrashCategory.CAN),
	TIN_CAN("통조림캔", TrashCategory.CAN),
	DISPOSABLE_DRINK_CUP("일회용음료수잔", TrashCategory.PLASTIC),
	PET_BOTTLE("페트병", TrashCategory.PET_BOTTLE),
	LARGE_PLASTIC_CONTAINER("대용량플라스틱통", TrashCategory.PLASTIC),
	WASTE_CONTAINER("밀폐용기", TrashCategory.PLASTIC),
	BATHROOM_SUPPLIES("욕실용품", TrashCategory.PLASTIC);

	private final String name;
	private final TrashCategory category;
}

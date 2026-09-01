package com.example.plogrid.domain.trash.service.analysis;

import java.util.Arrays;
import java.util.Map;

import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;
import com.example.plogrid.global.apiPayload.code.TrashErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

final class TrashLabelResolver {

	private static final Map<String, TrashCategory> MATERIAL_LABEL_TO_CATEGORY = Map.of(
		"유리", TrashCategory.GLASS,
		"투명 플라스틱", TrashCategory.PET_BOTTLE
	);

	private TrashLabelResolver() {
	}

	static TrashSubCategory resolveSubCategory(String className) {
		return Arrays.stream(TrashSubCategory.values())
			.filter(subCategory -> subCategory.getName().equals(className))
			.findFirst()
			.orElseThrow(() -> new GeneralException(TrashErrorCode.UNKNOWN_TRASH_CLASS));
	}

	static TrashCategory resolveCategoryByMaterial(String materialLabel) {
		TrashCategory category = MATERIAL_LABEL_TO_CATEGORY.get(materialLabel);
		if (category == null) {
			throw new GeneralException(TrashErrorCode.UNKNOWN_TRASH_CLASS);
		}
		return category;
	}
}

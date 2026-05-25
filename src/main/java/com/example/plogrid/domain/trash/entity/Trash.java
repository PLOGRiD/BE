package com.example.plogrid.domain.trash.entity;

import com.example.plogrid.domain.common.BaseEntity;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trash extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plogging_id", nullable = false)
	private Plogging plogging;

	@Column(nullable = false)
	private String trashImage;

	@Column(nullable = false)
	private double latitude;

	@Column(nullable = false)
	private double longitude;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TrashCategory category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TrashSubCategory subCategory;
}

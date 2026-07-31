package com.example.plogrid.domain.recruitment.entity;

import java.time.LocalDateTime;

import com.example.plogrid.domain.common.BaseEntity;
import com.example.plogrid.domain.recruitment.entity.enums.RecruitmentStatus;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Recruitment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private String thumbnailImageUrl;

	@Column(nullable = false)
	private LocalDateTime eventDateTime;

	@Column(nullable = false)
	private String eventLocation;

	@Column(nullable = false)
	private Integer maxParticipants;

	@Column(nullable = false)
	private Integer currentParticipants;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_id", nullable = false)
	private Host host;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RecruitmentStatus status;

}
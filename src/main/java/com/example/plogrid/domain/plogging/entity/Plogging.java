package com.example.plogrid.domain.plogging.entity;

import com.example.plogrid.domain.common.BaseEntity;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;

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
public class Plogging extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "distance_meters", nullable = false)
	private double distanceMeters;

	@Column(name = "duration_seconds", nullable = false)
	private double durationSeconds;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PloggingStatus status;

	public static Plogging create(Member member) {
		return Plogging.builder()
			.member(member)
			.distanceMeters(0)
			.durationSeconds(0)
			.status(PloggingStatus.IN_PROGRESS)
			.build();
	}

	public void complete() {
		this.status = PloggingStatus.COMPLETED;
	}
}

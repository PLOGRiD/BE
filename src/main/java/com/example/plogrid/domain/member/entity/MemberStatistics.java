package com.example.plogrid.domain.member.entity;

import java.util.List;

import com.example.plogrid.domain.trash.entity.enums.TrashCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberStatistics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	private int ploggingCount;

	private double totalDistanceMeters;

	private double totalDurationSeconds;

	private int totalTrashCount;

	private int contributionScore;

	private int vinylCount;

	private int glassCount;

	private int paperCount;

	private int canCount;

	private int petCount;

	private int plasticCount;

	private int cigaretteButtCount;

	private int etcCount;

	public static MemberStatistics create(Member member) {
		return MemberStatistics.builder()
			.member(member)
			.build();
	}

	public void reflectPlogging(double distanceMeters, double durationSeconds, List<TrashCategory> categories, int contributionScore) {
		this.ploggingCount += 1;
		this.totalDistanceMeters += distanceMeters;
		this.totalDurationSeconds += durationSeconds;
		this.totalTrashCount += categories.size();
		this.contributionScore += contributionScore;

		for (TrashCategory category : categories) {
			switch (category) {
				case VINYL -> this.vinylCount++;
				case GLASS -> this.glassCount++;
				case PAPER -> this.paperCount++;
				case CAN -> this.canCount++;
				case PET_BOTTLE -> this.petCount++;
				case PLASTIC -> this.plasticCount++;
				case CIGARETTE -> this.cigaretteButtCount++;
				default -> this.etcCount++;
			}
		}
	}
}

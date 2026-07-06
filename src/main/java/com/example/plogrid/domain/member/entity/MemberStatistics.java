package com.example.plogrid.domain.member.entity;


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
}

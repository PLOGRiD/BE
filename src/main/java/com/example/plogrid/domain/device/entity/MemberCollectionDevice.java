package com.example.plogrid.domain.device.entity;

import com.example.plogrid.domain.common.BaseEntity;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.plogging.entity.Plogging;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "collection_device_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MemberCollectionDevice extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collection_device_id", nullable = false)
	private CollectionDevice collectionDevice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plogging_id")
	private Plogging plogging;

	public static MemberCollectionDevice create(Member member, CollectionDevice collectionDevice) {
		return MemberCollectionDevice.builder()
			.member(member)
			.collectionDevice(collectionDevice)
			.build();
	}

	public void linkPlogging(Plogging plogging) {
		this.plogging = plogging;
	}
}

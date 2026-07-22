package com.example.plogrid.domain.chat.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.plogrid.domain.common.BaseEntity;
import com.example.plogrid.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class ChatSession extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	private String sessionTitle;

	@Builder.Default
	@OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatLog> chatLogs = new ArrayList<>();

	public static ChatSession create(Member member, String sessionTitle) {
		return ChatSession.builder()
			.member(member)
			.sessionTitle(sessionTitle)
			.build();
	}
}
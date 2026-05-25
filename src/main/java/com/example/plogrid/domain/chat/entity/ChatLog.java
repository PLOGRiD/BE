package com.example.plogrid.domain.chat.entity;

import com.example.plogrid.domain.chat.entity.enums.ChatRole;
import com.example.plogrid.domain.chat.entity.enums.MessageType;
import com.example.plogrid.domain.common.BaseEntity;

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
public class ChatLog extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String ChatContent;

	@Column(nullable = true)
	private String chatImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ChatRole chatRole;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_type", nullable = false)
	private MessageType messageType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_session_id", nullable = false)
	private ChatSession chatSession;
}

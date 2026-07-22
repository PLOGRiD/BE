package com.example.plogrid.domain.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.chat.converter.ChatConverter;
import com.example.plogrid.domain.chat.dto.ChatResponseDTO;
import com.example.plogrid.domain.chat.entity.ChatSession;
import com.example.plogrid.domain.chat.repository.ChatSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

	private final ChatSessionRepository chatSessionRepository;

	public List<ChatResponseDTO.SessionSummary> getSessions(Long memberId) {
		List<ChatSession> chatSessions = chatSessionRepository.findByMemberIdOrderByLastMessageAtDesc(memberId);

		return ChatConverter.toSessionSummaryList(chatSessions);
	}
}

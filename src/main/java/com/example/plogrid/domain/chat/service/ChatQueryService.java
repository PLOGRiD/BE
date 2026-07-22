package com.example.plogrid.domain.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.chat.converter.ChatConverter;
import com.example.plogrid.domain.chat.dto.ChatResponseDTO;
import com.example.plogrid.domain.chat.entity.ChatLog;
import com.example.plogrid.domain.chat.entity.ChatSession;
import com.example.plogrid.domain.chat.repository.ChatLogRepository;
import com.example.plogrid.domain.chat.repository.ChatSessionRepository;
import com.example.plogrid.global.apiPayload.code.ChatErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

	private final ChatSessionRepository chatSessionRepository;
	private final ChatLogRepository chatLogRepository;

	public List<ChatResponseDTO.SessionSummary> getSessions(Long memberId) {
		List<ChatSession> chatSessions = chatSessionRepository.findByMemberIdOrderByLastMessageAtDesc(memberId);

		return ChatConverter.toSessionSummaryList(chatSessions);
	}

	public List<ChatResponseDTO.ChatLogResponse> getMessages(Long memberId, Long chatSessionId) {
		ChatSession chatSession = chatSessionRepository.findById(chatSessionId)
			.orElseThrow(() -> new GeneralException(ChatErrorCode.CHAT_SESSION_NOT_FOUND));

		if (!chatSession.getMember().getId().equals(memberId)) {
			throw new GeneralException(ChatErrorCode.CHAT_SESSION_NOT_FOUND);
		}

		List<ChatLog> chatLogs = chatLogRepository.findByChatSessionIdOrderByCreatedAtAsc(chatSessionId);

		return ChatConverter.toChatLogResponseList(chatLogs);
	}
}

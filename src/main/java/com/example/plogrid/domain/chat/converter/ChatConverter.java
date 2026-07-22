package com.example.plogrid.domain.chat.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import com.example.plogrid.domain.chat.dto.ChatResponseDTO;
import com.example.plogrid.domain.chat.entity.ChatLog;
import com.example.plogrid.domain.chat.entity.ChatSession;

public class ChatConverter {

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA);

	private ChatConverter() {
	}

	public static ChatResponseDTO.MessageResponse toMessageResponse(ChatSession chatSession, ChatLog assistantLog) {
		return ChatResponseDTO.MessageResponse.builder()
			.chatSessionId(chatSession.getId())
			.chatLogId(assistantLog.getId())
			.message(assistantLog.getChatContent())
			.messageType(assistantLog.getMessageType())
			.createdAt(assistantLog.getCreatedAt())
			.build();
	}

	public static List<ChatResponseDTO.SessionSummary> toSessionSummaryList(List<ChatSession> chatSessions) {
		return chatSessions.stream()
			.map(chatSession -> ChatResponseDTO.SessionSummary.builder()
				.chatSessionId(chatSession.getId())
				.sessionTitle(chatSession.getSessionTitle())
				.lastMessageAt(formatLastMessageAt(chatSession.getLastMessageAt()))
				.build())
			.toList();
	}

	private static String formatLastMessageAt(LocalDateTime lastMessageAt) {
		if (lastMessageAt == null) {
			return null;
		}

		LocalDate today = LocalDate.now();
		LocalDate date = lastMessageAt.toLocalDate();
		long daysBetween = ChronoUnit.DAYS.between(date, today);

		if (daysBetween <= 0) {
			return lastMessageAt.format(TIME_FORMATTER);
		}
		if (daysBetween == 1) {
			return "어제";
		}

		return daysBetween + "일전";
	}

	public static List<ChatResponseDTO.ChatLogResponse> toChatLogResponseList(List<ChatLog> chatLogs) {
		return chatLogs.stream()
			.map(chatLog -> ChatResponseDTO.ChatLogResponse.builder()
				.chatLogId(chatLog.getId())
				.chatRole(chatLog.getChatRole())
				.message(chatLog.getChatContent())
				.imageUrl(chatLog.getChatImageUrl())
				.messageType(chatLog.getMessageType())
				.createdAt(chatLog.getCreatedAt())
				.build())
			.toList();
	}
}

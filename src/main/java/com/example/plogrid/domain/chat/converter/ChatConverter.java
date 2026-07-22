package com.example.plogrid.domain.chat.converter;

import com.example.plogrid.domain.chat.dto.ChatResponseDTO;
import com.example.plogrid.domain.chat.entity.ChatLog;
import com.example.plogrid.domain.chat.entity.ChatSession;

public class ChatConverter {

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
}

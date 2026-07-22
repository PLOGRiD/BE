package com.example.plogrid.domain.chat.dto;

import java.time.LocalDateTime;

import com.example.plogrid.domain.chat.entity.enums.MessageType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class ChatResponseDTO {

	@Schema(name = "채팅 질의 응답")
	@Builder
	@Getter
	public static class MessageResponse {

		@Schema(description = "채팅 세션 ID", example = "1")
		private Long chatSessionId;

		@Schema(description = "채팅 로그 ID (assistant 응답 기준)", example = "12")
		private Long chatLogId;

		@Schema(description = "챗봇 답변 텍스트", example = "이 쓰레기는 플라스틱류로 분리배출 하시면 됩니다.")
		private String message;

		@Schema(description = "메시지 타입", example = "TEXT")
		private MessageType messageType;

		@Schema(description = "생성 시각", example = "2026-07-23T15:30:00")
		private LocalDateTime createdAt;
	}
}

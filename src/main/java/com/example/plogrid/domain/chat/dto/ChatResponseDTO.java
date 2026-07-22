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

	@Schema(name = "채팅 세션 요약")
	@Builder
	@Getter
	public static class SessionSummary {

		@Schema(description = "채팅 세션 ID", example = "1")
		private Long chatSessionId;

		@Schema(description = "채팅 세션 제목", example = "이 쓰레기는 어떻게 분리배출...")
		private String sessionTitle;

		@Schema(description = "마지막 메시지 시간 (당일: 오전/오후 h:mm, 어제, 그 외: N일전)", example = "오후 1:12")
		private String lastMessageAt;
	}
}

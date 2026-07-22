package com.example.plogrid.domain.chat.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatRequestDTO {

	@Schema(name = "채팅 질의 요청")
	@Getter
	@Setter
	@NoArgsConstructor
	public static class MessageRequest {

		@Schema(description = "채팅 세션 ID (null인 경우 새 세션 생성)", example = "1")
		private Long chatSessionId;

		@Schema(description = "질의 텍스트", example = "이 쓰레기는 어떻게 분리배출 해야 해?")
		@NotBlank(message = "메시지를 입력해주세요.")
		private String message;

		@Schema(description = "첨부 이미지 (최대 1장, 선택)", type = "string", format = "binary")
		private MultipartFile image;
	}

	@Schema(name = "채팅 세션 삭제 요청")
	@Getter
	@NoArgsConstructor
	public static class SessionDeleteRequest {

		@Schema(description = "삭제할 채팅 세션 ID 목록", example = "[1, 2, 3]")
		@NotEmpty(message = "삭제할 세션 ID를 1개 이상 입력해주세요.")
		private List<Long> chatSessionIds;
	}
}
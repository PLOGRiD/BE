package com.example.plogrid.domain.chat.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.chat.dto.ChatRequestDTO;
import com.example.plogrid.domain.chat.dto.ChatResponseDTO;
import com.example.plogrid.domain.chat.service.ChatCommandService;
import com.example.plogrid.domain.chat.service.ChatQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Chat", description = "챗봇 API")
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

	private final ChatCommandService chatCommandService;
	private final ChatQueryService chatQueryService;

	@Operation(
		summary = "채팅 질의 요청 API",
		description = """
			챗봇에게 텍스트 메시지(및 선택적으로 이미지 1장)를 질의하고 답변을 받습니다.

			- `chatSessionId`가 없으면 새 채팅 세션이 생성되고, 세션 제목은 첫 메시지 내용을 기반으로 자동 생성됩니다.
			- `chatSessionId`가 있으면 기존 세션에 이어서 대화가 저장됩니다. 다른 회원의 세션 ID를 넘기면 거절됩니다.
			- `message`는 필수이며, `image`는 최대 1장까지 선택적으로 첨부할 수 있습니다.
			- 요청은 `multipart/form-data` 형식이어야 합니다.
			"""
	)
	@PostMapping(consumes = "multipart/form-data")
	public ApiResponse<ChatResponseDTO.MessageResponse> requestChat(
		@AuthUser Long memberId, @Valid @ModelAttribute ChatRequestDTO.MessageRequest request) {
		return ApiResponse.onSuccess(chatCommandService.chat(memberId, request));
	}

	@Operation(
		summary = "단일 폐기물 분리배출 방법 질의 API",
		description = """
			이미지 하나로 분리배출 방법만 조회합니다. 채팅 세션이나 대화내역 없이 단발성으로 답변만 받습니다.

			- `imageUrl`은 필수이며, 이미지에 접근 가능한 URL이어야 합니다.
			"""
	)
	@PostMapping("/wastes")
	public ApiResponse<ChatResponseDTO.TrashChatMessageResponse> requestChat(
		@Valid @RequestBody ChatRequestDTO.TrashMessageRequest request) {
		return ApiResponse.onSuccess(chatCommandService.wasteSortingMethod(request.getImageUrl()));
	}

	@Operation(
		summary = "채팅 세션 목록 조회 API",
		description = """
			로그인한 회원의 채팅 세션 목록을 조회합니다.

			- 각 세션의 마지막 메시지 시간(`lastMessageAt`) 기준 최신순으로 정렬됩니다.
			- 세션 ID(`chatSessionId`), 제목(`sessionTitle`), 마지막 메시지 시간(`lastMessageAt`)을 반환합니다.
			"""
	)
	@GetMapping
	public ApiResponse<List<ChatResponseDTO.SessionSummary>> getSessions(@AuthUser Long memberId) {
		return ApiResponse.onSuccess(chatQueryService.getSessions(memberId));
	}

	@Operation(
		summary = "채팅 세션 메시지 조회 API",
		description = """
			선택한 채팅 세션의 지난 메시지 목록을 오래된 순으로 조회합니다.

			- 본인 소유의 세션이 아니거나 존재하지 않는 세션이면 거절됩니다.
			"""
	)
	@GetMapping("/{chatSessionId}/messages")
	public ApiResponse<List<ChatResponseDTO.ChatLogResponse>> getMessages(
		@AuthUser Long memberId, @PathVariable Long chatSessionId) {
		return ApiResponse.onSuccess(chatQueryService.getMessages(memberId, chatSessionId));
	}

	@Operation(
		summary = "채팅 세션 삭제 API",
		description = """
			선택한 채팅 세션들을 삭제합니다.

			- 여러 세션 ID를 한 번에 넘겨 일괄 삭제할 수 있습니다.
			- 세션에 속한 모든 채팅 로그가 함께 완전히 삭제되며, 복구할 수 없습니다.
			- 하나라도 본인 소유의 세션이 아니거나 존재하지 않으면 전체 요청이 거절되고 아무 것도 삭제되지 않습니다.
			"""
	)
	@DeleteMapping
	public ApiResponse<Void> deleteSessions(
		@AuthUser Long memberId, @Valid @RequestBody ChatRequestDTO.SessionDeleteRequest request) {
		chatCommandService.deleteSessions(memberId, request.getChatSessionIds());
		return ApiResponse.onSuccess(null);
	}
}
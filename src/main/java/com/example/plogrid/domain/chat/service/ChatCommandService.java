package com.example.plogrid.domain.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.domain.chat.converter.ChatConverter;
import com.example.plogrid.domain.chat.dto.ChatRequestDTO;
import com.example.plogrid.domain.chat.dto.ChatResponseDTO;
import com.example.plogrid.domain.chat.entity.ChatLog;
import com.example.plogrid.domain.chat.entity.ChatSession;
import com.example.plogrid.domain.chat.entity.enums.ChatRole;
import com.example.plogrid.domain.chat.entity.enums.MessageType;
import com.example.plogrid.domain.chat.repository.ChatLogRepository;
import com.example.plogrid.domain.chat.repository.ChatSessionRepository;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.global.apiPayload.code.ChatErrorCode;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

	private static final int SESSION_TITLE_MAX_LENGTH = 20;

	private final ChatBotService chatBotService;
	private final ChatSessionRepository chatSessionRepository;
	private final ChatLogRepository chatLogRepository;
	private final MemberRepository memberRepository;

	public ChatResponseDTO.MessageResponse chat(Long memberId, ChatRequestDTO.MessageRequest request) {
		ChatSession chatSession = resolveChatSession(memberId, request.getChatSessionId(), request.getMessage());

		String userImageUrl = resolveImageUrl(request.getImage());
		MessageType userMessageType = userImageUrl != null ? MessageType.TEXT_IMAGE : MessageType.TEXT;

		chatLogRepository.save(
			ChatLog.create(chatSession, request.getMessage(), userImageUrl, ChatRole.USER, userMessageType));

		String answer = chatBotService.ask(request.getMessage(), userImageUrl);

		ChatLog assistantLog = chatLogRepository.save(
			ChatLog.create(chatSession, answer, null, ChatRole.ASSISTANT, MessageType.TEXT));

		return ChatConverter.toMessageResponse(chatSession, assistantLog);
	}

	private ChatSession resolveChatSession(Long memberId, Long chatSessionId, String firstMessage) {
		if (chatSessionId == null) {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

			return chatSessionRepository.save(ChatSession.create(member, generateSessionTitle(firstMessage)));
		}

		ChatSession chatSession = chatSessionRepository.findById(chatSessionId)
			.orElseThrow(() -> new GeneralException(ChatErrorCode.CHAT_SESSION_NOT_FOUND));

		if (!chatSession.getMember().getId().equals(memberId)) {
			// 세션 존재 여부를 노출하지 않기 위해 소유자가 아닌 경우에도 동일하게 404 처리
			throw new GeneralException(ChatErrorCode.CHAT_SESSION_NOT_FOUND);
		}

		return chatSession;
	}

	private String resolveImageUrl(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return null;
		}

		// TODO: 실제 이미지 스토리지(S3 등) 연동 전까지 더미 URL 사용
		return "https://dummy.plogrid.com/chat/" + image.getOriginalFilename();
	}

	private String generateSessionTitle(String message) {
		return message.length() > SESSION_TITLE_MAX_LENGTH
			? message.substring(0, SESSION_TITLE_MAX_LENGTH) + "..."
			: message;
	}
}

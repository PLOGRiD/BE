package com.example.plogrid.domain.chat.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.domain.chat.converter.ChatConverter;
import com.example.plogrid.domain.chat.dto.ChatBotHistoryDTO;
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
import com.example.plogrid.global.s3.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

	private static final int SESSION_TITLE_MAX_LENGTH = 20;
	private static final int HISTORY_TURN_LIMIT = 10;
	private static final int HISTORY_MESSAGE_LIMIT = HISTORY_TURN_LIMIT * 2;

	private final ChatBotService chatBotService;
	private final ChatSessionRepository chatSessionRepository;
	private final ChatLogRepository chatLogRepository;
	private final MemberRepository memberRepository;
	private final S3Uploader s3Uploader;

	public ChatResponseDTO.MessageResponse chat(Long memberId, ChatRequestDTO.MessageRequest request) {
		ChatSession chatSession = resolveChatSession(memberId, request.getChatSessionId(), request.getMessage());

		List<ChatBotHistoryDTO> history = resolveHistory(chatSession.getId());

		String userImageUrl = resolveImageUrl(request.getImage());
		MessageType userMessageType = userImageUrl != null ? MessageType.TEXT_IMAGE : MessageType.TEXT;

		chatLogRepository.save(
			ChatLog.create(chatSession, request.getMessage(), userImageUrl, ChatRole.USER, userMessageType));

		String answer = chatBotService.ask(
			chatSession.getId().toString(), request.getMessage(), request.getImage(), history);

		ChatLog assistantLog = chatLogRepository.save(
			ChatLog.create(chatSession, answer, null, ChatRole.ASSISTANT, MessageType.TEXT));

		chatSession.updateLastMessageAt(assistantLog.getCreatedAt());

		return ChatConverter.toMessageResponse(chatSession, assistantLog);
	}

	public ChatResponseDTO.TrashChatMessageResponse wasteSortingMethod(String imageUrl) {
		String answer = chatBotService.askWasteSortingMethod(imageUrl);
		return ChatConverter.toTrashChatMessageResponse(answer);
	}

	public void deleteSessions(Long memberId, List<Long> chatSessionIds) {
		List<ChatSession> chatSessions = chatSessionIds.stream()
			.map(chatSessionId -> findOwnedSession(memberId, chatSessionId))
			.toList();

		chatSessions.stream()
			.flatMap(chatSession -> chatSession.getChatLogs().stream())
			.map(ChatLog::getChatImageUrl)
			.filter(Objects::nonNull)
			.forEach(s3Uploader::deleteImage);

		chatSessionRepository.deleteAll(chatSessions);
	}

	private ChatSession resolveChatSession(Long memberId, Long chatSessionId, String firstMessage) {
		if (chatSessionId == null) {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

			return chatSessionRepository.save(ChatSession.create(member, generateSessionTitle(firstMessage)));
		}

		return findOwnedSession(memberId, chatSessionId);
	}

	private ChatSession findOwnedSession(Long memberId, Long chatSessionId) {
		ChatSession chatSession = chatSessionRepository.findById(chatSessionId)
			.orElseThrow(() -> new GeneralException(ChatErrorCode.CHAT_SESSION_NOT_FOUND));

		if (!chatSession.getMember().getId().equals(memberId)) {
			throw new GeneralException(ChatErrorCode.CHAT_SESSION_NOT_FOUND);
		}

		return chatSession;
	}

	private List<ChatBotHistoryDTO> resolveHistory(Long chatSessionId) {
		List<ChatLog> recentChatLogsDesc = chatLogRepository.findByChatSessionIdOrderByCreatedAtDesc(
			chatSessionId, PageRequest.of(0, HISTORY_MESSAGE_LIMIT));

		return ChatConverter.toHistory(recentChatLogsDesc);
	}

	private String resolveImageUrl(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return null;
		}

		return s3Uploader.uploadChatImage(image);
	}

	private String generateSessionTitle(String message) {
		return message.length() > SESSION_TITLE_MAX_LENGTH
			? message.substring(0, SESSION_TITLE_MAX_LENGTH) + "..."
			: message;
	}
}

package com.example.plogrid.domain.chat.service;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.domain.chat.dto.ChatBotHistoryDTO;
import com.example.plogrid.domain.chat.dto.ChatBotResponseDTO;
import com.example.plogrid.domain.chat.dto.ChatBotWasteRequestDTO;
import com.example.plogrid.global.apiPayload.code.ChatErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotService {

	private static final String FALLBACK_ANSWER = "죄송해요, 지금은 답변을 생성하지 못했어요. 잠시 후 다시 시도해주세요.";

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate = new RestTemplateBuilder()
		.connectTimeout(Duration.ofSeconds(5))
		.readTimeout(Duration.ofSeconds(60))
		.build();

	@Value("${chatbot.url}")
	private String chatbotUrl;

	@Value("${chatbot.waste-url}")
	private String wasteUrl;

	public String askWasteSortingMethod(String imageUrl) {
		ChatBotWasteRequestDTO body = new ChatBotWasteRequestDTO(imageUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<ChatBotWasteRequestDTO> requestEntity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<ChatBotResponseDTO> response = restTemplate.exchange(
				wasteUrl,
				HttpMethod.POST,
				requestEntity,
				ChatBotResponseDTO.class
			);

			String answer = response.getBody() != null ? response.getBody().getAnswer() : null;
			return StringUtils.hasText(answer) ? answer : FALLBACK_ANSWER;
		} catch (RestClientException e) {
			log.error("[챗봇 서버 통신 오류] wasteUrl={}", wasteUrl, e);
			throw new GeneralException(ChatErrorCode.CHATBOT_SERVER_ERROR);
		}
	}

	public String ask(String sessionId, String message, MultipartFile image, List<ChatBotHistoryDTO> history) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("session_id", sessionId);
		body.add("message", message);

		if (image != null && !image.isEmpty()) {
			body.add("image", toFileEntity(image));
		}

		if (!CollectionUtils.isEmpty(history)) {
			body.add("history", toHistoryJson(history));
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<ChatBotResponseDTO> response = restTemplate.exchange(
				chatbotUrl,
				HttpMethod.POST,
				requestEntity,
				ChatBotResponseDTO.class
			);

			String answer = response.getBody() != null ? response.getBody().getAnswer() : null;
			return StringUtils.hasText(answer) ? answer : FALLBACK_ANSWER;
		} catch (RestClientException e) {
			log.error("[챗봇 서버 통신 오류] chatbotUrl={}", chatbotUrl, e);
			throw new GeneralException(ChatErrorCode.CHATBOT_SERVER_ERROR);
		}
	}

	private String toHistoryJson(List<ChatBotHistoryDTO> history) {
		try {
			return objectMapper.writeValueAsString(history);
		} catch (JsonProcessingException e) {
			log.error("[챗봇 히스토리 직렬화 오류]", e);
			throw new GeneralException(ChatErrorCode.CHATBOT_SERVER_ERROR);
		}
	}

	private HttpEntity<ByteArrayResource> toFileEntity(MultipartFile image) {
		HttpHeaders fileHeaders = new HttpHeaders();
		String contentType = image.getContentType();
		fileHeaders.setContentType(
			contentType != null ? MediaType.parseMediaType(contentType) : MediaType.IMAGE_PNG
		);

		ByteArrayResource resource;
		try {
			resource = new ByteArrayResource(image.getBytes()) {
				@Override
				public String getFilename() {
					return image.getOriginalFilename();
				}
			};
		} catch (IOException e) {
			log.error("[챗봇 이미지 첨부 변환 오류]", e);
			throw new GeneralException(ChatErrorCode.CHATBOT_SERVER_ERROR);
		}

		return new HttpEntity<>(resource, fileHeaders);
	}
}

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.domain.chat.dto.ChatBotResponseDTO;
import com.example.plogrid.global.apiPayload.code.ChatErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

@Service
public class ChatBotService {

	private static final String FALLBACK_ANSWER = "죄송해요, 지금은 답변을 생성하지 못했어요. 잠시 후 다시 시도해주세요.";

	private final RestTemplate restTemplate = new RestTemplateBuilder()
		.connectTimeout(Duration.ofSeconds(5))
		.readTimeout(Duration.ofSeconds(60))
		.build();

	@Value("${chatbot.url}")
	private String chatbotUrl;

	public String ask(String sessionId, String message, MultipartFile image) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("session_id", sessionId);
		body.add("message", message);

		if (image != null && !image.isEmpty()) {
			body.add("image", toFileEntity(image));
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
			throw new GeneralException(ChatErrorCode.CHATBOT_SERVER_ERROR);
		}

		return new HttpEntity<>(resource, fileHeaders);
	}
}

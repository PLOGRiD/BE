package com.example.plogrid.global.sse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

	public static final String SSE_CHANNEL = "sse-channel";

	private final Map<Long, SseEmitter> ploggingEmitters = new ConcurrentHashMap<>();
	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;

	public SseEmitter getSseEmitter(Long memberId) {
		SseEmitter sseEmitter = new SseEmitter(30 * 60 * 1000L);

		ploggingEmitters.put(memberId, sseEmitter);

		sseEmitter.onCompletion(() -> removeEmitter(memberId, sseEmitter));
		sseEmitter.onTimeout(() -> removeEmitter(memberId, sseEmitter));
		sseEmitter.onError(e -> removeEmitter(memberId, sseEmitter));

		return sseEmitter;
	}

	private void removeEmitter(Long memberId, SseEmitter emitter) {
		ploggingEmitters.remove(memberId, emitter);
	}

	public void send(Long memberId, String eventName, Object data) {
		try {
			String payload = objectMapper.writeValueAsString(new SseMessage(memberId, eventName, data));
			stringRedisTemplate.convertAndSend(SSE_CHANNEL, payload);
		} catch (JsonProcessingException e) {
			log.error("SSE 메시지 발행 실패 - memberId: {}, eventName: {}", memberId, eventName, e);
		}
	}

	public void sendLocal(Long memberId, String eventName, Object data) {
		SseEmitter emitter = ploggingEmitters.get(memberId);
		if (emitter == null) {
			return;
		}

		try {
			emitter.send(SseEmitter.event()
				.name(eventName)
				.data(data)
			);
		} catch (IOException e) {
			removeEmitter(memberId, emitter);
		}
	}
}

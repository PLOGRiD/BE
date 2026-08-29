package com.example.plogrid.global.sse;

import java.io.IOException;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseRedisSubscriber implements MessageListener {

	private final SseService sseService;
	private final ObjectMapper objectMapper;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			SseMessage sseMessage = objectMapper.readValue(message.getBody(), SseMessage.class);
			sseService.sendLocal(sseMessage.memberId(), sseMessage.eventName(), sseMessage.data());
		} catch (IOException e) {
			log.error("SSE 메시지 역직렬화 실패", e);
		}
	}
}

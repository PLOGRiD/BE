package com.example.plogrid.global.sse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class sseService {

	private final Map<Long, SseEmitter> ploggingEmitters = new ConcurrentHashMap<>();

	public SseEmitter getSseEmitter(Long ploggingId) {
		SseEmitter sseEmitter = new SseEmitter(30 * 60 * 1000L);

		ploggingEmitters.put(ploggingId, sseEmitter);

		sseEmitter.onCompletion(() -> removeEmitter(ploggingId, sseEmitter));
		sseEmitter.onTimeout(() -> removeEmitter(ploggingId, sseEmitter));
		sseEmitter.onError(e -> removeEmitter(ploggingId, sseEmitter));

		return sseEmitter;
	}

	private void removeEmitter(Long ploggingId, SseEmitter emitter) {
		ploggingEmitters.remove(ploggingId, emitter);
	}

	public void send(Long ploggingId, Object data) {
		SseEmitter emitter = ploggingEmitters.get(ploggingId);
		if (emitter == null) {
			return;
		}

		try {
			emitter.send(SseEmitter.event()
				.name("plogging-in-progress")
				.data(data)
			);
		} catch (IOException e) {
			removeEmitter(ploggingId, emitter);
		}
	}
}

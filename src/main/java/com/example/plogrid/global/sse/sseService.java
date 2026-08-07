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

	public void send(Long memberId, Object data) {
		SseEmitter emitter = ploggingEmitters.get(memberId);
		if (emitter == null) {
			return;
		}

		try {
			emitter.send(SseEmitter.event()
				.name("plogging-in-progress")
				.data(data)
			);
		} catch (IOException e) {
			removeEmitter(memberId, emitter);
		}
	}
}

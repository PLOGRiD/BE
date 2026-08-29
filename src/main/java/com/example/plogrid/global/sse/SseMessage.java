package com.example.plogrid.global.sse;

public record SseMessage(
	Long memberId,
	String eventName,
	Object data
) {
}

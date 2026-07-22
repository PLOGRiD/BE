package com.example.plogrid.domain.chat.service;

import org.springframework.stereotype.Service;

@Service
public class ChatBotService {

	// TODO: 실제 챗봇 서버 연동 전까지 고정 응답을 반환하는 임시 구현
	public String ask(String message, String imageUrl) {
		return "아직 챗봇 서버 연동 전이라 임시 응답을 드립니다. 질문: \"" + message + "\"";
	}
}

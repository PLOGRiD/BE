package com.example.plogrid.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatBotHistoryDTO {

	private String role;
	private String content;
}

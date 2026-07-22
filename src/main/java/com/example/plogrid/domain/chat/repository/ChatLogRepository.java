package com.example.plogrid.domain.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.chat.entity.ChatLog;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

	List<ChatLog> findByChatSessionIdOrderByCreatedAtAsc(Long chatSessionId);
}

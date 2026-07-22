package com.example.plogrid.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.chat.entity.ChatLog;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
}

package com.example.plogrid.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.chat.entity.ChatSession;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
}

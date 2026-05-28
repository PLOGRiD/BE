package com.example.plogrid.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}

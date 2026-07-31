package com.example.plogrid.domain.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.post.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

	int countByPostId(Long postId);

	Optional<Like> findByMemberIdAndPostId(Long memberId, Long postId);
}

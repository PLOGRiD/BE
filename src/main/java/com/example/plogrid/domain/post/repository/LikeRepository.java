package com.example.plogrid.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plogrid.domain.post.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

	int countByPostId(Long postId);
}

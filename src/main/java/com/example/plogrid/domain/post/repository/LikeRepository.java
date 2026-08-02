package com.example.plogrid.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.plogrid.domain.post.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

	int countByPostId(Long postId);

	Optional<Like> findByMemberIdAndPostId(Long memberId, Long postId);

	@Query("select l.post.id from Like l where l.member.id = :memberId and l.post.id in :postIds")
	List<Long> findLikedPostIds(@Param("memberId") Long memberId, @Param("postIds") List<Long> postIds);
}

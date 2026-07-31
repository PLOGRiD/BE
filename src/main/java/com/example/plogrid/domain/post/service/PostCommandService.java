package com.example.plogrid.domain.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.domain.post.converter.PostConverter;
import com.example.plogrid.domain.post.dto.PostResponseDTO;
import com.example.plogrid.domain.post.entity.Like;
import com.example.plogrid.domain.post.entity.Post;
import com.example.plogrid.domain.post.repository.LikeRepository;
import com.example.plogrid.domain.post.repository.PostRepository;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.code.PostErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService {

	private final PostRepository postRepository;
	private final LikeRepository likeRepository;
	private final MemberRepository memberRepository;

	public PostResponseDTO.LikeToggleResponseDTO toggleLike(Long memberId, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new GeneralException(PostErrorCode.POST_NOT_FOUND));

		boolean isLiked = likeRepository.findByMemberIdAndPostId(memberId, postId)
			.map(like -> {
				likeRepository.delete(like);
				return false;
			})
			.orElseGet(() -> {
				Member member = memberRepository.findById(memberId)
					.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));
				likeRepository.save(Like.create(member, post));
				return true;
			});

		int likeCount = likeRepository.countByPostId(postId);

		return PostConverter.toLikeToggleResponseDTO(isLiked, likeCount);
	}
}

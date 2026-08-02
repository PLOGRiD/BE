package com.example.plogrid.domain.post.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.post.converter.PostConverter;
import com.example.plogrid.domain.post.dto.PostResponseDTO;
import com.example.plogrid.domain.post.entity.Post;
import com.example.plogrid.domain.post.repository.LikeRepository;
import com.example.plogrid.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

	private final PostRepository postRepository;
	private final LikeRepository likeRepository;

	public PostResponseDTO.InfoListResponseDTO getInfoList(Long memberId, Integer page, Integer size) {
		Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page - 1, size));

		List<Long> postIds = posts.getContent().stream()
			.map(Post::getId)
			.toList();

		Map<Long, Integer> likeCounts = posts.getContent().stream()
			.collect(Collectors.toMap(Post::getId, post -> likeRepository.countByPostId(post.getId())));

		Set<Long> likedPostIds = new HashSet<>(likeRepository.findLikedPostIds(memberId, postIds));

		return PostConverter.toInfoListResponseDTO(posts, likeCounts, likedPostIds);
	}
}

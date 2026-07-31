package com.example.plogrid.domain.post.service;

import java.util.Map;
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

	public PostResponseDTO.InfoListResponseDTO getInfoList(Integer page, Integer size) {
		Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page - 1, size));

		Map<Long, Integer> likeCounts = posts.getContent().stream()
			.collect(Collectors.toMap(Post::getId, post -> likeRepository.countByPostId(post.getId())));

		return PostConverter.toInfoListResponseDTO(posts, likeCounts);
	}
}

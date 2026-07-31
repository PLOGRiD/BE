package com.example.plogrid.domain.post.converter;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.plogrid.domain.post.dto.PostResponseDTO;
import com.example.plogrid.domain.post.entity.Post;
import com.example.plogrid.domain.post.entity.PostImage;

public class PostConverter {

	private static final String DUMMY_AUTHOR_NICKNAME = "익명의 플로거";
	private static final String DUMMY_AUTHOR_PROFILE_IMAGE_URL = "https://default.png";

	private PostConverter() {
	}

	public static PostResponseDTO.InfoListResponseDTO toInfoListResponseDTO(Page<Post> posts, Map<Long, Integer> likeCounts) {
		return PostResponseDTO.InfoListResponseDTO.builder()
			.listSize(posts.getNumberOfElements())
			.totalPage(posts.getTotalPages())
			.totalElements(posts.getTotalElements())
			.isFirst(posts.isFirst())
			.isLast(posts.isLast())
			.infos(posts.getContent().stream()
				.map(post -> toInfoResponseDTO(post, likeCounts.getOrDefault(post.getId(), 0)))
				.toList())
			.build();
	}

	public static PostResponseDTO.InfoResponseDTO toInfoResponseDTO(Post post, int likeCount) {
		return PostResponseDTO.InfoResponseDTO.builder()
			.authorNickname(DUMMY_AUTHOR_NICKNAME)
			.authorProfileImageUrl(DUMMY_AUTHOR_PROFILE_IMAGE_URL)
			.postContent(post.getPostContent())
			.createdAt(post.getCreatedAt())
			.imageUrls(post.getPostImages().stream()
				.map(PostImage::getPostImageUrl)
				.toList())
			.likeCount(likeCount)
			.build();
	}
}

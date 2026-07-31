package com.example.plogrid.domain.post.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.post.dto.PostResponseDTO;
import com.example.plogrid.domain.post.service.PostCommandService;
import com.example.plogrid.domain.post.service.PostQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "Post", description = "커뮤니티(INFO) API")
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

	private final PostQueryService postQueryService;
	private final PostCommandService postCommandService;

	@Operation(
		summary = "INFO 게시글 목록 조회 API",
		description = """
        INFO 게시글 목록을 최신순으로 페이징 처리하여 조회합니다.
        Parameter:
        - `page`: 페이지 번호 (1부터 시작)
        - `size`: 한 페이지당 게시글 개수 (기본값: 10)
    """
	)
	@GetMapping
	public ApiResponse<PostResponseDTO.InfoListResponseDTO> getInfoList(
		@RequestParam(name = "page", defaultValue = "1") @Min(1)Integer page,
		@RequestParam(name = "size", defaultValue = "10") @Min(1)Integer size
	) {
		return ApiResponse.onSuccess(postQueryService.getInfoList(page, size));
	}

	@Operation(
		summary = "INFO 게시글 좋아요 토글 API",
		description = """
        게시글에 대한 좋아요를 등록/취소합니다.
        이미 좋아요를 누른 상태라면 취소되고, 누르지 않은 상태라면 등록됩니다.
        Parameter:
        - `postId`: 게시글 ID
    """
	)
	@PostMapping("/{postId}/like")
	public ApiResponse<PostResponseDTO.LikeToggleResponseDTO> toggleLike(
		@AuthUser Long memberId,
		@PathVariable Long postId
	) {
		return ApiResponse.onSuccess(postCommandService.toggleLike(memberId, postId));
	}

}

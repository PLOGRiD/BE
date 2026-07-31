package com.example.plogrid.domain.post.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.post.dto.PostResponseDTO;
import com.example.plogrid.domain.post.service.PostQueryService;
import com.example.plogrid.global.apiPayload.ApiResponse;

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

}

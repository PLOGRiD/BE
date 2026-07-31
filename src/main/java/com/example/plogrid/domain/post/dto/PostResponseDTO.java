package com.example.plogrid.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class PostResponseDTO {

	@Schema(name = "INFO 목록 응답")
	@Builder
	@Getter
	public static class InfoListResponseDTO {
		private Integer listSize;
		private Integer totalPage;
		private Long totalElements;
		private Boolean isFirst;
		private Boolean isLast;
		private List<InfoResponseDTO> infos;
	}

	@Schema(name = "INFO 개별 응답")
	@Builder
	@Getter
	public static class InfoResponseDTO {
		private String authorNickname;
		private String authorProfileImageUrl;
		private String postContent;
		private LocalDateTime createdAt;
		private List<String> imageUrls;
		private int likeCount;
	}
}

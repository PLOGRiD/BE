package com.example.plogrid.global.s3;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * EC2 instance role 기반 S3 업로드 인프라(권한/네트워크) 검증용 임시 컨트롤러.
 * 검증이 끝나면 이 컨트롤러와 SecurityConfig의 "/api/v1/test/**" permitAll 항목을 함께 삭제할 것.
 */
@Tag(name = "[TEMP] S3 Test", description = "S3 업로드 인프라 검증용 임시 API - 검증 후 삭제 예정")
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class S3TestController {

	private final S3Uploader s3Uploader;

	@Operation(summary = "[임시] S3 업로드 테스트", description = "EC2 role로 실제 S3 업로드가 되는지 확인용. 인증 불필요.")
	@PostMapping(value = "/s3-upload", consumes = "multipart/form-data")
	public ApiResponse<String> testUpload(@RequestParam MultipartFile file) {
		return ApiResponse.onSuccess(s3Uploader.uploadTrashImage(file));
	}
}

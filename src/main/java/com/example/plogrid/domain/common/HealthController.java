package com.example.plogrid.domain.common;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Health API")
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

	@Operation(summary = "헬스 체크 API")
	@GetMapping
	public ApiResponse<Map<String, String>> health() {
		return ApiResponse.onSuccess(Map.of("status", "UP"));
	}
}

package com.example.plogrid.domain.device.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plogrid.domain.device.dto.DeviceRequestDTO;
import com.example.plogrid.domain.device.service.DeviceCommandService;
import com.example.plogrid.global.apiPayload.ApiResponse;
import com.example.plogrid.global.security.handler.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Device", description = "수거 디바이스 API")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

	private final DeviceCommandService deviceCommandService;

	@Operation(
		summary = "수거 디바이스 연동 API",
		description = "플로깅 시작 시, 현재 로그인한 회원 계정에 수거 디바이스를 연동합니다."
	)
	@PostMapping("/link")
	public ApiResponse<Void> linkDevice(@AuthUser Long memberId, @RequestBody @Valid DeviceRequestDTO.LinkRequest request) {
		deviceCommandService.linkDevice(memberId, request);
		return ApiResponse.onSuccess(null);
	}
}

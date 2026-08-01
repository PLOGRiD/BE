package com.example.plogrid.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import com.example.plogrid.global.apiPayload.dto.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrashErrorCode implements BaseErrorCode {

	INVALID_VIEWPORT(HttpStatus.BAD_REQUEST, "TRASH400_1", "뷰포트 범위가 올바르지 않습니다. minLat/minLng는 maxLat/maxLng보다 작거나 같아야 합니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDTO getReason() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}

	@Override
	public ErrorReasonDTO getReasonHttpStatus() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build();
	}
}

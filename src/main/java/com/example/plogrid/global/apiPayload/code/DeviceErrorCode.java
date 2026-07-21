package com.example.plogrid.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import com.example.plogrid.global.apiPayload.dto.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceErrorCode implements BaseErrorCode{

	DEVICE_NOT_LINKED(HttpStatus.UNAUTHORIZED, "DEVICE401_1", "연동되지 않은 디바이스입니다."),
	INVALID_DEVICE_CREDENTIALS(HttpStatus.UNAUTHORIZED, "DEVICE401_2", "디바이스 정보가 올바르지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "DEVICE401_3", "유효하지 않은 Refresh 토큰입니다."),
	DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "DEVICE404_1", "존재하지 않는 디바이스입니다."),
	DEVICE_ALREADY_LINKED(HttpStatus.CONFLICT, "DEVICE409_1", "이미 다른 회원과 연동된 디바이스입니다."),
	MEMBER_ALREADY_LINKED(HttpStatus.CONFLICT, "DEVICE409_2", "이미 다른 디바이스와 연동된 회원입니다.");

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

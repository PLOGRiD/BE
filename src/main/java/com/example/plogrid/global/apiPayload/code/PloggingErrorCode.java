package com.example.plogrid.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import com.example.plogrid.global.apiPayload.dto.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PloggingErrorCode implements BaseErrorCode {

	PLOGGING_NOT_IN_PROGRESS(HttpStatus.NOT_FOUND, "PLOGGING404_1", "진행 중인 플로깅이 없습니다."),
	PLOGGING_NOT_FOUND(HttpStatus.NOT_FOUND, "PLOGGING404_2", "플로깅 기록이 없습니다."),
	PLOGGING_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PLOGGING403_1", "본인의 플로깅만 조회할 수 있습니다.");

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

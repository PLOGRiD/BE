package com.example.plogrid.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import com.example.plogrid.global.apiPayload.dto.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitmentErrorCode implements BaseErrorCode {

	RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "RECRUITMENT404_1", "존재하지 않는 모집글입니다."),
	RECRUITMENT_FULL(HttpStatus.CONFLICT, "RECRUITMENT409_1", "모집 정원이 마감되었습니다."),
	RECRUITMENT_ENDED(HttpStatus.CONFLICT, "RECRUITMENT409_2", "이미 종료된 이벤트입니다.");

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

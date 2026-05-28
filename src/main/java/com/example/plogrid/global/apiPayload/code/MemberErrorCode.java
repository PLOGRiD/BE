package com.example.plogrid.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import com.example.plogrid.global.apiPayload.dto.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "존재하지 않는 회원입니다."),
	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "MEMBER409_1", "이미 사용 중인 아이디입니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER409_2", "이미 사용 중인 이메일입니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER401_1", "비밀번호가 올바르지 않습니다."),
	ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "MEMBER400_1", "이미 탈퇴한 회원입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER401_2", "유효하지 않은 Refresh 토큰입니다.");

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

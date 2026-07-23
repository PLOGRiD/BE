package com.example.plogrid.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import com.example.plogrid.global.apiPayload.dto.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {

	S3_FILE_EMPTY(HttpStatus.BAD_REQUEST, "S3400_1", "업로드할 파일이 비어 있습니다."),
	S3_INVALID_URL(HttpStatus.BAD_REQUEST, "S3400_2", "올바르지 않은 S3 이미지 URL입니다."),
	S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3500_1", "파일 업로드 중 오류가 발생했습니다."),
	S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3500_2", "파일 삭제 중 오류가 발생했습니다.");

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

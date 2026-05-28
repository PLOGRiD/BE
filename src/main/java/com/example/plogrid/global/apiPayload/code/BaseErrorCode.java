package com.example.plogrid.global.apiPayload.code;

import com.example.plogrid.global.apiPayload.dto.ErrorReasonDTO;

public interface BaseErrorCode {

    ErrorReasonDTO getReason();

    ErrorReasonDTO getReasonHttpStatus();
}

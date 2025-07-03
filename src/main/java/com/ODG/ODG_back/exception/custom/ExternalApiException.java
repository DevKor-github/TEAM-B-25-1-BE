package com.ODG.ODG_back.exception.custom;

import com.ODG.ODG_back.exception.ErrorCode;

public class ExternalApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detail;


    public ExternalApiException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + "[" + detail + "]");
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetail() {
        return detail;
    }
}

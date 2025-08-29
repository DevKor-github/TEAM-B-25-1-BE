package com.ODG.ODG_back.exception.custom;

import com.ODG.ODG_back.exception.ErrorCode;

public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

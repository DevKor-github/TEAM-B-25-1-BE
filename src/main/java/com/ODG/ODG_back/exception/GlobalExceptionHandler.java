package com.ODG.ODG_back.exception;

import com.ODG.ODG_back.dto.exception.ErrorResponse;
import com.ODG.ODG_back.exception.custom.BadRequestException;
import com.ODG.ODG_back.exception.custom.ExternalApiException;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.exception.custom.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotFoundException(NotFoundException e) {
        return buildResponse(e.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    // 400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handlerBadRequestException(BadRequestException e) {
        return buildResponse(e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    // 401 Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handlerUnauthorizedException(UnauthorizedException e) {
        return buildResponse(e.getErrorCode(), HttpStatus.UNAUTHORIZED);
    }

    // 500 Internal Server Error
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handlerRuntimeException(RuntimeException e) {
        log.error("Internal Server Error", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 502, 503 External Error
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handlerExternalApiException(ExternalApiException e) {
        return buildResponse(e.getErrorCode(),
                HttpStatus.valueOf(Integer.parseInt(e.getErrorCode().getCode())));
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()),
                status);
    }
}

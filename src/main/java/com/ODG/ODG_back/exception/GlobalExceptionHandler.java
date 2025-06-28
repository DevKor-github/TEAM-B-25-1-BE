package com.ODG.ODG_back.exception;

import com.ODG.ODG_back.dto.exception.ErrorResponse;
import com.ODG.ODG_back.exception.custom.BadRequestException;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.exception.custom.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotFoundException(NotFoundException e) {
        ErrorResponse response = new ErrorResponse("404", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 400 Bad Request
    @ExceptionHandler({
            BadRequestException.class,
            IllegalArgumentException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorResponse> handlerBadRequestException(RuntimeException e) {
        ErrorResponse response = new ErrorResponse("400", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 401 Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handlerUnauthorizedException(UnauthorizedException e) {
        ErrorResponse response = new ErrorResponse("401", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 500 Internal Server Error
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handlerRuntimeException(RuntimeException e) {
        ErrorResponse response = new ErrorResponse("500", "Internal Server Error: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

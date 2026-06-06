package com.bookmyvenue.backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ){
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                errorCode.name(),
                errorCode.getMessage(),
                errorCode.getHttpStatus().value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}

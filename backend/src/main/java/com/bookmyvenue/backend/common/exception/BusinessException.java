package com.bookmyvenue.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(HttpStatus httpStatus, ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

package com.hotel.hotel_service.exception;

import com.hotel.hotel_service.enums.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String details;
    private Class<?> exceptionOccuredClass;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public CustomException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    public CustomException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public CustomException(ErrorCode errorCode, String details, Class<?> exceptionClass) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
        this.exceptionOccuredClass = exceptionClass;
    }
}

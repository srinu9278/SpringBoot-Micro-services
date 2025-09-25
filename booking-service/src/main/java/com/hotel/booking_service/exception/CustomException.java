package com.hotel.booking_service.exception;

import com.hotel.booking_service.enums.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String details;
    private final String exceptionOccurredClass;
    
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
        this.exceptionOccurredClass = this.getClass().getSimpleName();
    }
    
    public CustomException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
        this.exceptionOccurredClass = this.getClass().getSimpleName();
    }
    
    public CustomException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.details = details;
        this.exceptionOccurredClass = this.getClass().getSimpleName();
    }
}



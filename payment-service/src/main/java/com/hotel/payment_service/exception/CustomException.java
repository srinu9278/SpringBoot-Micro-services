package com.hotel.payment_service.exception;

import com.hotel.payment_service.enums.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String details;
    private final String exceptionOccuredClass;
    
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
        this.exceptionOccuredClass = this.getClass().getSimpleName();
    }
    
    public CustomException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
        this.exceptionOccuredClass = this.getClass().getSimpleName();
    }
    
    public CustomException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.details = details;
        this.exceptionOccuredClass = this.getClass().getSimpleName();
    }
}



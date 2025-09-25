package com.hotel.payment_service.exception;

import com.hotel.payment_service.dto.ErrorResponseDto;
import com.hotel.payment_service.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException ex, WebRequest request) {
        log.error("Custom exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
            ex.getErrorCode().getCode(),
            ex.getErrorCode().getMessage(),
            ex.getDetails(),
            request.getDescription(false).replace("uri=", "")
        );
        
        HttpStatus status = getHttpStatus(ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, status);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("HTTP message not readable: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
            ErrorCode.VALIDATION_ERROR.getCode(),
            "Invalid JSON format or missing required fields",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
            ErrorCode.VALIDATION_ERROR.getCode(),
            "Validation failed",
            errors.toString(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            "An unexpected error occurred",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case PAYMENT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case PAYMENT_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case INVALID_PAYMENT_DATA, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case PAYMENT_PROCESSING_FAILED -> HttpStatus.UNPROCESSABLE_ENTITY;
            case INVALID_PAYMENT_STATUS, REFUND_NOT_ALLOWED, REFUND_AMOUNT_EXCEEDED, PAYMENT_EXPIRED -> HttpStatus.UNPROCESSABLE_ENTITY;
            case GATEWAY_ERROR, INSUFFICIENT_FUNDS, CARD_DECLINED, INVALID_CARD, DUPLICATE_TRANSACTION -> HttpStatus.PAYMENT_REQUIRED;
            case DATABASE_ERROR, INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case HOTELID_NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }
}

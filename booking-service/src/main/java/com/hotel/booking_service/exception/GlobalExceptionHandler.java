package com.hotel.booking_service.exception;

import com.hotel.booking_service.dto.ErrorResponseDto;
import com.hotel.booking_service.enums.ErrorCode;
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

@Slf4j
@RestControllerAdvice
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
        
        HttpStatus status = getHttpStatusFromErrorCode(ex.getErrorCode());
        return ResponseEntity.status(status).body(errorResponse);
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
        
        String details = "Validation failed for fields: " + errors.toString();
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
            ErrorCode.VALIDATION_ERROR.getCode(),
            ErrorCode.VALIDATION_ERROR.getMessage(),
            details,
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("HTTP message not readable exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
            ErrorCode.INVALID_BOOKING_DATA.getCode(),
            "Invalid JSON format or missing required fields",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
            "An unexpected error occurred",
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    private HttpStatus getHttpStatusFromErrorCode(ErrorCode errorCode) {
        return switch (errorCode) {
            case BOOKING_NOT_FOUND, BOOKING_DATA_NOT_FOUND, INVALID_BOOKING_REFERENCE -> HttpStatus.NOT_FOUND;
            case BOOKING_ALREADY_EXISTS, BOOKING_REFERENCE_ALREADY_EXISTS, INVALID_BOOKING_DATA, 
                 INVALID_BOOKING_STATUS, INVALID_PAYMENT_STATUS, INVALID_DATE_RANGE, 
                 INVALID_GUEST_DATA, INVALID_AMOUNT_CALCULATION, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case ROOM_NOT_AVAILABLE, BOOKING_CONFLICT, BOOKING_ALREADY_CONFIRMED, 
                 BOOKING_ALREADY_CANCELLED, BOOKING_ALREADY_CHECKED_IN, BOOKING_ALREADY_CHECKED_OUT -> HttpStatus.CONFLICT;
            case BOOKING_CANNOT_BE_CANCELLED, BOOKING_CANNOT_BE_MODIFIED, BOOKING_UPDATE_FAILED, 
                 BOOKING_DELETE_FAILED, BOOKING_CANCELLATION_FAILED, BOOKING_CONFIRMATION_FAILED, 
                 BOOKING_CHECK_IN_FAILED, BOOKING_CHECK_OUT_FAILED, BOOKING_EXPIRED, 
                 PAYMENT_REQUIRED, DATABASE_ERROR -> HttpStatus.UNPROCESSABLE_ENTITY;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case HOTELID_ROOMID_USERID_NOT_EXISTS -> HttpStatus.NOT_FOUND;
        };
    }
}


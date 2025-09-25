package com.hotel.user_service.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message){
        super(message);
    }
}

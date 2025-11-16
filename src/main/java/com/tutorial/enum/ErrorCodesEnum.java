package com.tutorial.Enum;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodesEnum {
    SUCCESS(HttpStatus.OK, "Successful"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username already exists"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USERNAME_EMPTY(HttpStatus.BAD_REQUEST, "Username cannot be empty"),
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "Password cannot be empty"),
    UNACCEPTABLE_USERNAME(HttpStatus.BAD_REQUEST, "Username does not meet requirements"),
    UNACCEPTABLE_PASSWORD(HttpStatus.BAD_REQUEST, "Password does not meet requirements"),
    UNACCEPTABLE_AUTHORITY(HttpStatus.BAD_REQUEST, "Authority does not meet requirements"),
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurred"),
    UNLUCKY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "RandomGenerator Error");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCodesEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

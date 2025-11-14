package com.tutorial.Enum;

import lombok.Getter;

@Getter
public enum ErrorCodesEnum {
    SUCCESS(200, "Registration successful"),
    USERNAME_ALREADY_EXISTS(409, "Username already exists"),
    USER_NOT_FOUND(404, "User not found"),
    USERNAME_EMPTY(400, "Username cannot be empty"),
    PASSWORD_EMPTY(400, "Password cannot be empty"),
    UNACCEPTABLE_USERNAME(400, "Username does not meet requirements"),
    UNACCEPTABLE_PASSWORD(400, "Password does not meet requirements"),
    UNACCEPTABLE_AUTHORITY(400, "Authority does not meet requirements"),
    DB_ERROR(500, "Database error occurred");

    private final int httpStatusCode;
    private final String message;

    ErrorCodesEnum(int httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }
}

package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;
import lombok.Getter;

@Getter
public class AuthorityServiceException extends RuntimeException {
    private final ErrorCodesEnum code;
    public AuthorityServiceException(ErrorCodesEnum code, String message) {
        super(message);
        this.code = code;
    }
}
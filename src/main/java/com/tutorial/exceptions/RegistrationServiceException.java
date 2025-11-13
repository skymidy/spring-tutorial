package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;
import lombok.Getter;

@Getter
public class RegistrationServiceException extends RuntimeException {
    private final ErrorCodesEnum code;
    public RegistrationServiceException(ErrorCodesEnum code, String message) {
        super(message);
        this.code = code;
    }
}

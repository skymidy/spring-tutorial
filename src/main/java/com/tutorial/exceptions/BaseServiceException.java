package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;
import lombok.Getter;

@Getter
public abstract class BaseServiceException extends RuntimeException {

    private final ErrorCodesEnum code;

    public BaseServiceException(ErrorCodesEnum code, String message) {
        super(message);
        this.code = code;
    }

    public BaseServiceException(ErrorCodesEnum code) {
        super(code.getMessage());
        this.code = code;
    }
}

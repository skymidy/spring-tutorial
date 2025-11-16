package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class ApiKeyServiceException extends BaseServiceException {
    public ApiKeyServiceException(ErrorCodesEnum code, String message) {
        super(code, message);
    }

    public ApiKeyServiceException(ErrorCodesEnum code) {
        super(code);
    }
}

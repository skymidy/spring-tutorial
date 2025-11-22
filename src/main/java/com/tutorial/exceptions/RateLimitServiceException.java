package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class RateLimitServiceException extends BaseServiceException {
    public RateLimitServiceException(ErrorCodesEnum code, String message) {
        super(code, message);
    }

    public RateLimitServiceException(ErrorCodesEnum code) {
        super(code);
    }
}

package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class PasswordServiceException extends BaseServiceException {
    public PasswordServiceException(ErrorCodesEnum code, String message) {
        super(code, message);
    }
    public PasswordServiceException(ErrorCodesEnum code) {
        super(code);
    }
}

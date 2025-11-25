package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class UserServiceException extends BaseServiceException {
    public UserServiceException(ErrorCodesEnum code, String message) {
        super(code, message);
    }

    public UserServiceException(ErrorCodesEnum code) {
        super(code);
    }
}

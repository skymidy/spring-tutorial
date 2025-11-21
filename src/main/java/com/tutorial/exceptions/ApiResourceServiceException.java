package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class ApiResourceServiceException extends BaseServiceException {
    public ApiResourceServiceException(ErrorCodesEnum code, String message) {
        super(code, message);
    }
    public ApiResourceServiceException(ErrorCodesEnum code) {
        super(code);
    }
}

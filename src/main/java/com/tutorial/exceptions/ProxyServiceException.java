package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class ProxyServiceException extends BaseServiceException {

    public ProxyServiceException(ErrorCodesEnum code, String message) {
        super(code, message);
    }

    public ProxyServiceException(ErrorCodesEnum code) {
        super(code);
    }
}
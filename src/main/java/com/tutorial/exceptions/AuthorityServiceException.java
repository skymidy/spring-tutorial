package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class AuthorityServiceException extends BaseServiceException {

    public AuthorityServiceException(ErrorCodesEnum code, String message) {
        super(code,message);
    }
    public AuthorityServiceException(ErrorCodesEnum code) {
        super(code);
    }
}
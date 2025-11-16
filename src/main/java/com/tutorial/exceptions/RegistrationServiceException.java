package com.tutorial.exceptions;

import com.tutorial.Enum.ErrorCodesEnum;

public class RegistrationServiceException extends BaseServiceException {
    public RegistrationServiceException(ErrorCodesEnum code, String message) {
        super(code,message);
    }
    public RegistrationServiceException(ErrorCodesEnum code) {
        super(code);
    }
}

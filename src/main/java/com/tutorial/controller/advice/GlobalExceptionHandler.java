package com.tutorial.controller.advice;

import com.tutorial.exceptions.*;
import com.tutorial.model.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> catchRegistrationServiceException(RegistrationServiceException e) {
        return genericResponse(e,"RegistrationService Exception! Code: {}, Message: {}");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> catchRegistrationServiceException(AuthorityServiceException e) {
        return genericResponse(e,"AuthorityService Exception! Code: {}, Message: {}");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> catchApiKeyServiceException(ApiKeyServiceException e) {
        return genericResponse(e,"ApiKeyService Exception! Code: {}, Message: {}");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> catchApiKeyServiceException(PasswordServiceException e) {
        return genericResponse(e,"ApiKeyService Exception! Code: {}, Message: {}");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> catchApiResourceServiceException(ApiResourceServiceException e) {
        return genericResponse(e,"ApiKeyService Exception! Code: {}, Message: {}");
    }


    private ResponseEntity<ErrorResponseDto> genericResponse(BaseServiceException e,String messageTemplate) {
        HttpStatus status = e.getCode().getHttpStatus();
        String message = e.getMessage();
        log.error("RegistrationServiceException status: {}, message: {}", status, message, e);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDto(status.value(), message));
    }

}

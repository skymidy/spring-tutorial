package com.tutorial.controller.advice;

import com.tutorial.exceptions.AuthorityServiceException;
import com.tutorial.exceptions.BaseServiceException;
import com.tutorial.exceptions.RegistrationServiceException;
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
        return genericResponse(e,"AuthorityServiceException Exception! Code: {}, Message: {}");
    }

    private static ResponseEntity<ErrorResponseDto> genericResponse(BaseServiceException e,String messageTemplate) {
        HttpStatus status = e.getCode().getHttpStatus();
        String message = e.getMessage();
        log.error("RegistrationServiceException status: {}, message: {}", status, message, e);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDto(status.value(), message));
    }

}

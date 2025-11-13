package com.tutorial.service;

import com.tutorial.exceptions.RegistrationServiceException;
import com.tutorial.model.dto.RegistrationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<RegistrationErrorDto> catchRegistrationException(RegistrationServiceException e) {
        int code = e.getCode().getHttpStatusCode();
        String message = e.getMessage();
        log.error("code: {}, message: {}", code, message, e);
        return ResponseEntity
                .status(code)
                .body(new RegistrationErrorDto(code, message));
    }
}

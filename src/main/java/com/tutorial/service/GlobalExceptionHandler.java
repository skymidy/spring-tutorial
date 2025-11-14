package com.tutorial.service;

import com.tutorial.exceptions.AuthorityServiceException;
import com.tutorial.exceptions.RegistrationServiceException;
import com.tutorial.model.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> catchRegistrationException(RegistrationServiceException e) {
        int code = e.getCode().getHttpStatusCode();
        String message = e.getMessage();
        log.error("RegistrationServiceException code: {}, message: {}", code, message, e);
        return ResponseEntity
                .status(code)
                .body(new ErrorResponseDto(code, message));
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> catchAuthorityServiceException(AuthorityServiceException e) {
        int code = e.getCode().getHttpStatusCode();
        String message = e.getMessage();
        log.error("AuthorityServiceException code: {}, message: {}", code, message, e);
        return ResponseEntity
                .status(code)
                .body(new ErrorResponseDto(code, message));
    }
}

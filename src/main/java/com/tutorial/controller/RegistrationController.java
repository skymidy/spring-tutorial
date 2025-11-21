package com.tutorial.controller;

import com.tutorial.model.dto.RegistrationRequestDto;
import com.tutorial.model.dto.UserDto;
import com.tutorial.service.RegistrationService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto register(@RequestBody RegistrationRequestDto req) {
        return registrationService.register(req);
    }
}

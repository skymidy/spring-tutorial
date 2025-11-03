package com.tutorial.controller;

import com.tutorial.model.dto.RegistrationRequest;
import com.tutorial.service.RegistrationService;
import com.tutorial.service.RegistrationService.RegistrationResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {

  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegistrationRequest req) {
    RegistrationResult result = registrationService.register(req);
    if (!result.isSuccess()) {
      return ResponseEntity.badRequest().body(result.getMessage());
    }
    return ResponseEntity.status(201).body(result.getPayload());
  }
}

package com.tutorial.controller;

import com.tutorial.model.dto.PasswordDto;
import com.tutorial.model.dto.UserDto;
import com.tutorial.service.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto updatePassword(@PathVariable("username") String username,
                                  @RequestBody PasswordDto passwordDto){
        return passwordService.updatePassword(username, passwordDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/me")
    public UserDto updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody PasswordDto passwordDto){
        return passwordService.updatePassword(userDetails.getUsername(), passwordDto);
    }
}

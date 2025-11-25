package com.tutorial.controller;

import com.tutorial.model.dto.ApiKeyDto;
import com.tutorial.model.dto.UserDto;
import com.tutorial.model.dto.UsernameDto;
import com.tutorial.service.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apikey")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public ApiKeyDto getApiKey(@AuthenticationPrincipal UserDetails userDetails) {
        return apiKeyService.getUserApiKey(userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/me")
    public ApiKeyDto generateNewApiKey(@AuthenticationPrincipal UserDetails userDetails) {
        return apiKeyService.generateApiKeyForUser(userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/key")
    public UserDto getUserByApiKey(@RequestBody ApiKeyDto apiKeyDto) {
        return apiKeyService.getUserByApiKey(apiKeyDto.getApiKey());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/{username}")
    public ApiKeyDto getUserApiKey(@PathVariable("username") String username) {
        return apiKeyService.getUserApiKey(username);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/user/{username}")
    public ApiKeyDto generateNewApiKeyForUser(@RequestBody UsernameDto usernameDto) {
        return apiKeyService.generateApiKeyForUser(usernameDto.getUsername());
    }

}

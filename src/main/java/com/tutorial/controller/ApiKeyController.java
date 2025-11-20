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
@RequestMapping("/api/user/apikey")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public String getApiKey(@AuthenticationPrincipal UserDetails userDetails) {
        return apiKeyService.getUserApiKey(userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String getUserApiKey(@RequestBody UsernameDto usernameDto) {
        return apiKeyService.getUserApiKey(usernameDto.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String getUserByApiKey(@RequestBody UsernameDto usernameDto) {
        return apiKeyService.getUserApiKey(usernameDto.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public String generateNewApiKey(@AuthenticationPrincipal UserDetails userDetails){
        return apiKeyService.generateApiKeyForUser(userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public UserDto generateNewApiKeyForUser(@RequestBody ApiKeyDto apiKeyDto){
        return apiKeyService.getUserByApiKey(apiKeyDto.getApiKey());
    }

}

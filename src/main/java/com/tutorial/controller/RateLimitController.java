package com.tutorial.controller;

import com.tutorial.model.dto.RateLimitDto;
import com.tutorial.model.dto.UserDto;
import com.tutorial.service.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratelimit")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public RateLimitDto getRateLimit(@AuthenticationPrincipal UserDetails userDetails){
        return rateLimitService.getUserRateLimit(userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RateLimitDto getUserRateLimit(@PathVariable("username") String username){
        return rateLimitService.getUserRateLimit(username);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto updateUserRateLimit(@RequestBody RateLimitDto rateLimitDto, @PathVariable("username") String username) {
        return rateLimitService.updateUserRateLimit(username, rateLimitDto.getRateLimit());
    }


}

package com.tutorial.controller;

import com.tutorial.model.dto.CacheDto;
import com.tutorial.service.CacheService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cache")
public class CacheController {
    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/clear")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CacheDto clearCache() {
        return cacheService.clearCache().block();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/info")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CacheDto getMemoryUsage() {
        return cacheService.getRedisMemoryUsage().block();
    }

}
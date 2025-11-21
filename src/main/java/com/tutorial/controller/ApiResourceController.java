package com.tutorial.controller;

import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.service.ApiResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/resource")
public class ApiResourceController {

    private final ApiResourceService apiResourceService;

    public ApiResourceController(ApiResourceService service) {
        this.apiResourceService = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResourceDto create(@RequestBody ApiResourceDto dto) {
        return apiResourceService.create(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<ApiResourceDto> getAll() {
        return apiResourceService.getAllResources();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Set<ApiResourceDto> getAllEnabled() {
        return apiResourceService.getAllEnabledResources();
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{apiAlias}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResourceDto get(@PathVariable("apiAlias") String apiAlias) {
        return apiResourceService.findByAlias(apiAlias);
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{apiAlias}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResourceDto update(@PathVariable("apiAlias") String apiAlias, @RequestBody ApiResourceDto dto) {
        return apiResourceService.update(apiAlias, dto);
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{apiAlias}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public long delete(@PathVariable("apiAlias") String apiAlias) {
        return apiResourceService.delete(apiAlias);
    }
}

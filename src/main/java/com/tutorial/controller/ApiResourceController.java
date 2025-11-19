package com.tutorial.controller;

import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.service.ApiResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ApiResourceController {

  private final ApiResourceService apiResourceService;

  public ApiResourceController(ApiResourceService service) {
    this.apiResourceService = service;
  }

  @PostMapping
  public ResponseEntity<ApiResource> create(@RequestBody ApiResourceDto dto, @AuthenticationPrincipal UserDetails userDetails) {
    ApiResource r = apiResourceService.create(dto, userDetails);
    return ResponseEntity.status(201).body(r);
  }

  @GetMapping
  public ResponseEntity<List<ApiResource>> list(@AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(apiResourceService.listForCurrentUser(userDetails));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResource> get(@PathVariable("id") Integer id) {
    return apiResourceService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody ApiResourceDto dto, @AuthenticationPrincipal UserDetails userDetails) {
    try {
      ApiResource updated = apiResourceService.update(id, dto, userDetails);
      return ResponseEntity.ok(updated);
    } catch (SecurityException se) {
      return ResponseEntity.status(403).body(se.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable("id") Integer id, @AuthenticationPrincipal UserDetails userDetails) {
    try {
      apiResourceService.delete(id, userDetails);
      return ResponseEntity.noContent().build();
    } catch (SecurityException se) {
      return ResponseEntity.status(403).body(se.getMessage());
    }
  }
}

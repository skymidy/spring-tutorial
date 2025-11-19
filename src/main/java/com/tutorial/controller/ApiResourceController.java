package com.tutorial.controller;

import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.service.ApiResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ApiResourceController {

  private final ApiResourceService service;

  public ApiResourceController(ApiResourceService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ApiResource> create(@RequestBody ApiResourceDto dto) {
    ApiResource r = service.create(dto);
    return ResponseEntity.status(201).body(r);
  }

  @GetMapping
  public ResponseEntity<List<ApiResource>> list() {
    return ResponseEntity.ok(service.listForCurrentUser());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResource> get(@PathVariable("id") Integer id) {
    return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody ApiResourceDto dto) {
    try {
      ApiResource updated = service.update(id, dto);
      return ResponseEntity.ok(updated);
    } catch (SecurityException se) {
      return ResponseEntity.status(403).body(se.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
    try {
      service.delete(id);
      return ResponseEntity.noContent().build();
    } catch (SecurityException se) {
      return ResponseEntity.status(403).body(se.getMessage());
    }
  }
}

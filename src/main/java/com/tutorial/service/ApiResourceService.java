package com.tutorial.service;

import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.model.entity.User;
import com.tutorial.repository.ApiResourceRepository;
import com.tutorial.repository.UserRepository;
import com.tutorial.repository.AuthorityRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApiResourceService {

  private final ApiResourceRepository apiResourceRepository;
  private final UserRepository userRepository;
  private final AuthorityRepository authorityRepository;

  public ApiResourceService(ApiResourceRepository apiResourceRepository, UserRepository userRepository,
      AuthorityRepository authorityRepository) {
    this.apiResourceRepository = apiResourceRepository;
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
  }

  private String currentUsername() {
    Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return p == null ? null : p.toString();
  }

  public ApiResource create(ApiResourceDto dto) {
    String username = currentUsername();
    User owner = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("user not found"));
    ApiResource r = new ApiResource();
    r.setAuthenticationType(null); // caller must set or we can map later
    r.setName(dto.getName());
    r.setBaseUrl(dto.getBaseUrl());
    r.setIsEnabled(dto.getIsEnabled() == null ? true : dto.getIsEnabled());
    r.setApiKey(dto.getApiKey());
    r.setOwner(owner);
    return apiResourceRepository.save(r);
  }

  public Optional<ApiResource> findById(Integer id) {
    return apiResourceRepository.findById(id);
  }

  public List<ApiResource> listForCurrentUser() {
    String username = currentUsername();
    if (isAdmin(username)) {
      return apiResourceRepository.findAll();
    }
    return apiResourceRepository.findByOwner_Username(username);
  }

  public ApiResource update(Integer id, ApiResourceDto dto) {
    ApiResource existing = apiResourceRepository.findById(id).orElseThrow(() -> new IllegalStateException("not found"));
    String username = currentUsername();
    if (!isAdmin(username) && !existing.getOwner().getUsername().equals(username)) {
      throw new SecurityException("forbidden");
    }
    if (dto.getName() != null)
      existing.setName(dto.getName());
    if (dto.getBaseUrl() != null)
      existing.setBaseUrl(dto.getBaseUrl());
    if (dto.getIsEnabled() != null)
      existing.setIsEnabled(dto.getIsEnabled());
    if (dto.getApiKey() != null)
      existing.setApiKey(dto.getApiKey());
    return apiResourceRepository.save(existing);
  }

  public void delete(Integer id) {
    ApiResource existing = apiResourceRepository.findById(id).orElseThrow(() -> new IllegalStateException("not found"));
    String username = currentUsername();
    if (!isAdmin(username) && !existing.getOwner().getUsername().equals(username)) {
      throw new SecurityException("forbidden");
    }
    apiResourceRepository.deleteById(id);
  }

  private boolean isAdmin(String username) {
    if (username == null)
      return false;
    return authorityRepository.findByUsername(username).map(a -> a.getAuthority().name().equals("ADMIN")).orElse(false);
  }
}

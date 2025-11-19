package com.tutorial.service;

import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.model.entity.User;
import com.tutorial.repository.ApiResourceRepository;
import com.tutorial.repository.UserRepository;
import com.tutorial.repository.AuthorityRepository;
import org.springframework.security.core.userdetails.UserDetails;
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

  public ApiResource create(ApiResourceDto dto, UserDetails currentUser) {
    User owner = userRepository.findByUsername(currentUser.getUsername()).orElseThrow(() -> new IllegalStateException("user not found"));

    ApiResource newApiResource = new ApiResource();
    newApiResource.setAuthenticationType(null);
    newApiResource.setName(dto.getName());
    newApiResource.setBaseUrl(dto.getBaseUrl());
    newApiResource.setIsEnabled(dto.getIsEnabled() == null || dto.getIsEnabled());
    newApiResource.setApiKey(dto.getApiKey());
    newApiResource.setOwner(owner);
    return apiResourceRepository.save(newApiResource);
  }

  public Optional<ApiResource> findById(Integer id) {
    return apiResourceRepository.findById(id);
  }

  public List<ApiResource> listForCurrentUser(UserDetails currentUser) {
    if (isAdmin(currentUser.getUsername())) {
      return apiResourceRepository.findAll();
    }
    return apiResourceRepository.findByOwner_Username(currentUser.getUsername());
  }

  public ApiResource update(Integer id, ApiResourceDto dto,UserDetails currentUser) {
    ApiResource existing = apiResourceRepository.findById(id).orElseThrow(() -> new IllegalStateException("not found"));
    if (!isAdmin(currentUser.getUsername()) && !existing.getOwner().getUsername().equals(currentUser.getUsername())) {
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

  public void delete(Integer id,UserDetails currentUser) {
    ApiResource existing = apiResourceRepository.findById(id).orElseThrow(() -> new IllegalStateException("not found"));
    if (!isAllowed(existing, currentUser.getUsername())) {
      throw new SecurityException("forbidden");
    }
    apiResourceRepository.deleteById(id);
  }

  private boolean isAdmin(String username) {
    if (username == null)
      return false;
    return authorityRepository.findByUsername(username).map(a -> a.getAuthority().name().equals("ADMIN")).orElse(false);
  }

  private boolean isAllowed(ApiResource resource, String username) {
    return isAdmin(username) || resource.getOwner().getUsername().equals(username);
  }
}

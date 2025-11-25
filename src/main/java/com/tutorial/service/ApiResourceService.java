package com.tutorial.service;

import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.ApiResourceServiceException;
import com.tutorial.mapper.ApiResourceMapper;
import com.tutorial.mapper.AuthenticationTypeMapper;
import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.dto.AuthenticationTypeDto;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.model.entity.AuthenticationType;
import com.tutorial.repository.ApiResourceRepository;
import com.tutorial.repository.AuthenticationTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ApiResourceService {

    private final ApiResourceRepository apiResourceRepository;
    private final ApiResourceMapper apiResourceMapper;
    private final AuthenticationTypeRepository authenticationTypeRepository;
    private final AuthenticationTypeMapper authenticationTypeMapper;

    public ApiResourceService(ApiResourceRepository apiResourceRepository, ApiKeyService apiKeyService,
                              ApiResourceMapper apiResourceMapper, AuthenticationTypeRepository authenticationTypeRepository, AuthenticationTypeMapper authenticationTypeMapper) {
        this.apiResourceRepository = apiResourceRepository;
        this.apiResourceMapper = apiResourceMapper;
        this.authenticationTypeRepository = authenticationTypeRepository;
        this.authenticationTypeMapper = authenticationTypeMapper;
    }

    @Transactional
    public ApiResourceDto create(ApiResourceDto apiResourceDto) {

        ApiResource newApiResource = apiResourceMapper.toEntity(apiResourceDto);


        Optional<AuthenticationType> authenticationType = authenticationTypeRepository.findByName(apiResourceDto.getAuthenticationTypeName());
        if (authenticationType.isEmpty()) {
            AuthenticationType newAuthenticationType = authenticationTypeRepository.save(
                    authenticationTypeMapper.toEntity(new AuthenticationTypeDto(apiResourceDto.getAuthenticationTypeName()))
            );
            newApiResource.setAuthenticationType(newAuthenticationType);
        } else {
            newApiResource.setAuthenticationType(authenticationType.get());
        }

        newApiResource.setIsEnabled(false);

        return apiResourceMapper.toDto(apiResourceRepository.save(newApiResource));
    }

    public ApiResourceDto findById(Integer id) {
        return apiResourceMapper.toDto(
                apiResourceRepository.findById(id).orElseThrow(() ->
                        new ApiResourceServiceException(ErrorCodesEnum.API_RESOURCE_NOT_FOUND
                        )));
    }

    public ApiResourceDto findByAlias(String apiAlias) {
        return apiResourceMapper.toDto(
                apiResourceRepository.findByName(apiAlias).orElseThrow(() ->
                        new ApiResourceServiceException(ErrorCodesEnum.API_RESOURCE_NOT_FOUND
                        )));
    }

    public Set<ApiResourceDto> getAllResources() {
        return apiResourceMapper.toDtoSet(apiResourceRepository.findAll());
    }

    public Set<ApiResourceDto> getAllEnabledResources() {
        return apiResourceMapper.toDtoSet(apiResourceRepository.findAllByIsEnabledTrue());
    }

    public ApiResourceDto update(String apiAlias, ApiResourceDto dto) {
        return update(
                apiResourceRepository
                        .findByName(apiAlias)
                        .orElseThrow(() ->
                                new ApiResourceServiceException(ErrorCodesEnum.API_RESOURCE_NOT_FOUND
                                )),
                dto);
    }

    public ApiResourceDto update(Integer id, ApiResourceDto dto) {
        return update(
                apiResourceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ApiResourceServiceException(ErrorCodesEnum.API_RESOURCE_NOT_FOUND
                                )),
                dto);
    }

    private ApiResourceDto update(ApiResource entity, ApiResourceDto dto) {

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getBaseUrl() != null) entity.setBaseUrl(dto.getBaseUrl());
        if (dto.getIsEnabled() != null) entity.setIsEnabled(dto.getIsEnabled());

        if (dto.getAuthenticationTypeName() != null) {
            Optional<AuthenticationType> authenticationType = authenticationTypeRepository.findByName(dto.getAuthenticationTypeName());
            if (authenticationType.isEmpty()) {

                AuthenticationType newAuthenticationType = authenticationTypeRepository.save(
                        authenticationTypeMapper.toEntity(new AuthenticationTypeDto(dto.getAuthenticationTypeName()))
                );

                entity.setAuthenticationType(newAuthenticationType);
            } else {
                entity.setAuthenticationType(authenticationType.get());
            }
        }

        return apiResourceMapper.toDto(apiResourceRepository.save(entity));
    }

    public long delete(String apiAlias) {
        return apiResourceRepository.deleteByName(apiAlias);
    }
}

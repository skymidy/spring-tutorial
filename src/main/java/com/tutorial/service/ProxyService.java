package com.tutorial.service;

import com.tutorial.model.entity.ApiResource;
import com.tutorial.repository.ApiResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProxyService {

    private final ApiResourceRepository apiResourceRepository;

    public ProxyService(ApiResourceRepository apiResourceRepository) {
        this.apiResourceRepository = apiResourceRepository;
    }

    public ApiResource getResourceByAlias(String alias) {
        return apiResourceRepository.findByNameAndIsEnabledTrue(alias)
                .orElseThrow(() -> new IllegalArgumentException("API resource not found or disabled"));
    }

    public String buildTargetUrl(ApiResource resource, String remainingPath, String queryString) {
        String sanitizedBase = stripTrailingSlash(resource.getBaseUrl());
        String sanitizedPath = stripLeadingSlash(remainingPath == null ? "" : remainingPath);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(sanitizedBase);
        if (!sanitizedPath.isEmpty()) {
            builder.path("/").path(sanitizedPath);
        }
        if (queryString != null && !queryString.isEmpty()) {
            builder.query(queryString);
        }
        return builder.build(true).toUriString();
    }

    private String stripLeadingSlash(String s) {
        return s.startsWith("/") ? s.substring(1) : s;
    }

    private String stripTrailingSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}



package com.tutorial.model.dto;

import lombok.Data;

@Data
public class ApiResourceDto {
    private String authenticationTypeName;
    private String name;
    private String baseUrl;
    private Boolean isEnabled;
    private String apiKey;
    private String targetAuthHeader;
}

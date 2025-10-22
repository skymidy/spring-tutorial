package com.tutorial.dto;

import lombok.Data;

@Data
public class ApiResourceDto {
  private Integer authenticationTypeId;
  private String authenticationTypeName;
  private String name;
  private String baseUrl;
  private Boolean isEnabled;
  private String apiKey;
}

package com.tutorial.model.dto;

import lombok.Data;

@Data
public class UserDto {
  private String username;
  private String apiKey;
  private Long rateLimit;
  private boolean enabled;
}

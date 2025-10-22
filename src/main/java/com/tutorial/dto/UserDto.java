package com.tutorial.dto;

import lombok.Data;

@Data
public class UserDto {
  private String username;
  private String apiKey;
  private Long roleId; 
  private String roleName;
  private Long rateLimit;
}

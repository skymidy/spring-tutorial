package com.tutorial.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private String username;
    private String apiKey;
    private Long rateLimit;
    private boolean enabled;
    private Set<String> authorities;
}

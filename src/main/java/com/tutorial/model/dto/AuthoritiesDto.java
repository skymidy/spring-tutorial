package com.tutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthoritiesDto {
    private Set<String> authorities;
}

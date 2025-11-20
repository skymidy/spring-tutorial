package com.tutorial.model.dto;

import com.tutorial.Enum.AuthorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthorityDto {
    private Set<String> authorities;
}

package com.tutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CacheDto {
    private Long cacheUsed;
    private String unit;
}

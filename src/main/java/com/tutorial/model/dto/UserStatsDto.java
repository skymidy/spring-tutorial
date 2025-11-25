package com.tutorial.model.dto;

import lombok.Data;

@Data
public class UserStatsDto {
    private String username;
    private Long totalRequests;
    private Long averageResponseTimeMs;
    private Double distinctResourcesUsed;
}

package com.tutorial.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TimeWindowStatsDto {
    private OffsetDateTime from;
    private OffsetDateTime to;
    private Long totalRequests;
    private Double averageResponseTimeMs;
}

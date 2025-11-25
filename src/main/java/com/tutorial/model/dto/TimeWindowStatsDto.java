package com.tutorial.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TimeWindowStatsDto {
    private OffsetDateTime fromDateTimeOffset;
    private OffsetDateTime toDateTimeOffset;
    private Long totalRequests;
    private Double averageResponseTimeMs;
}

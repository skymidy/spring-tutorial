package com.tutorial.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RequestLogDto {
    private Integer id;
    private Long userId;
    private Integer apiResourceId;
    private Integer authenticationTypeId;
    private String httpMethod;
    private String endpoint;
    private OffsetDateTime requestTimestamp;
    private Integer responseStatus;
    private Long responseTimeMs;
    private String responseBodyType;
    private boolean loadedFromCache;
}

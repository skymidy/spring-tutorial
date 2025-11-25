package com.tutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceStatsDto {
    private Integer apiResourceId;
    private String apiResourceName;
    private Long totalRequests;
    private Long getRequests;
    private Long postRequests;
    private Long putRequests;
    private Long deleteRequests;
    private Double averageResponseTimeMs;
    private Long errorCount;
    private Long cacheHits;


    private Double getErrorRate(){
        return ((double) errorCount)/totalRequests;
    }
    private Double getCacheHitRatio(){
        return ((double) cacheHits)/totalRequests;
    }
}

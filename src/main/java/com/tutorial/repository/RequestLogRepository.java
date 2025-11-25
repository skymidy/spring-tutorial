package com.tutorial.repository;


import com.tutorial.model.dto.ResourceStatsDto;
import com.tutorial.model.dto.TimeWindowStatsDto;
import com.tutorial.model.dto.UserStatsDto;
import com.tutorial.model.entity.RequestLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RequestLogRepository extends Repository<RequestLog, Integer> {
    RequestLog save(RequestLog log);

    Optional<RequestLog> findById(Integer id);

    @Query("""
            SELECT
            :fromDateTimeOffset AS fromDateTimeOffset,
            :toDateTimeOffset AS toDateTimeOffset,
            count(r) as totalRequests,
            avg(r.responseTimeMs) as averageResponseTimeMs
            FROM RequestLog r
            WHERE r.requestTimestamp BETWEEN :fromDateTimeOffset AND :toDateTimeOffset
            """)
    TimeWindowStatsDto aggregateStatsInTimeWindow(@Param("fromDateTimeOffset") OffsetDateTime from, @Param("toDateTimeOffset") OffsetDateTime to);


    @Query("""
            SELECT
            r.apiResource.id as apiResourceId,
            r.apiResource.name as apiResourceName,
            count(r) as totalRequests,
            sum(case when r.httpMethod = 'GET' then 1 else 0 end) as getRequests,
            sum(case when r.httpMethod = 'POST' then 1 else 0 end) as postRequests,
            sum(case when r.httpMethod = 'PUT' then 1 else 0 end) as putRequests,
            sum(case when r.httpMethod = 'DELETE' then 1 else 0 end) as deleteRequests,
            avg(r.responseTimeMs) as averageResponseTimeMs,
            sum(case when r.responseStatus >= 400 and r.responseStatus < 600 then 1 else 0 end) as errorCount,
            sum(case when r.loadedFromCache = true then 1 else 0 end) as cacheHits
            FROM RequestLog r
            WHERE r.requestTimestamp BETWEEN :fromDateTimeOffset AND :toDateTimeOffset
            GROUP BY r.apiResource.id
            """)
    List<ResourceStatsDto> findResourceAggregates(@Param("fromDateTimeOffset") OffsetDateTime from, @Param("toDateTimeOffset") OffsetDateTime to);

    @Query("""
            SELECT
            r.apiResource.id as apiResourceId,
            r.apiResource.name as apiResourceName,
            count(r) as totalRequests,
            sum(case when r.httpMethod = 'GET' then 1 else 0 end) as getRequests,
            sum(case when r.httpMethod = 'POST' then 1 else 0 end) as postRequests,
            sum(case when r.httpMethod = 'PUT' then 1 else 0 end) as putRequests,
            sum(case when r.httpMethod = 'DELETE' then 1 else 0 end) as deleteRequests,
            avg(r.responseTimeMs) as averageResponseTimeMs,
            sum(case when r.responseStatus >= 400 and r.responseStatus < 600 then 1 else 0 end) as errorCount,
            sum(case when r.loadedFromCache = true then 1 else 0 end) as cacheHits
            FROM RequestLog r
            WHERE r.requestTimestamp BETWEEN :fromDateTimeOffset AND :toDateTimeOffset AND r.apiResource.name = :alias
            GROUP BY r.apiResource.id
            """)
    List<ResourceStatsDto> findResourceAggregateByAlias(@Param("fromDateTimeOffset") OffsetDateTime from, @Param("toDateTimeOffset") OffsetDateTime to, @Param("alias") String alias);


    @Query(value = """
    SELECT
        ar.id AS apiResourceId,
        ar.name AS apiResourceName,
        COUNT(*) AS totalRequests,
        SUM(CASE WHEN r.http_method = 'GET' THEN 1 ELSE 0 END) AS getRequests,
        SUM(CASE WHEN r.http_method = 'POST' THEN 1 ELSE 0 END) AS postRequests,
        SUM(CASE WHEN r.http_method = 'PUT' THEN 1 ELSE 0 END) AS putRequests,
        SUM(CASE WHEN r.http_method = 'DELETE' THEN 1 ELSE 0 END) AS deleteRequests,
        AVG(r.response_time_ms) AS averageResponseTimeMs,
        SUM(CASE WHEN r.response_status >= 400 AND r.response_status < 600 THEN 1 ELSE 0 END) AS errorCount,
        SUM(CASE WHEN r.loaded_from_cache = TRUE THEN 1 ELSE 0 END) AS cacheHits
    FROM request_logs r
    JOIN api_resources ar ON r.api_resource_id = ar.id
    WHERE r.request_timestamp BETWEEN :fromDateTimeOffset AND :toDateTimeOffset
    GROUP BY ar.id
    ORDER BY totalRequests DESC
    LIMIT :topLimit
    """, nativeQuery = true)
    List<ResourceStatsDto> findTopResources(
            @Param("fromDateTimeOffset") OffsetDateTime from,
            @Param("toDateTimeOffset") OffsetDateTime to,
            @Param("topLimit") int top
    );


    @Query("""
            SELECT
            r.user.username as username,
            count(r) as totalRequests,
            count(distinct r.apiResource.id) as distinctResourcesUsed,
            avg(r.responseTimeMs) as averageResponseTimeMs
            FROM RequestLog r
            WHERE r.requestTimestamp BETWEEN :fromDateTimeOffset AND :toDateTimeOffset
            GROUP BY r.user.username
            """)
    List<UserStatsDto> findUsersAggregates(@Param("fromDateTimeOffset") OffsetDateTime from, @Param("toDateTimeOffset") OffsetDateTime to);

    @Query("""
            SELECT
            r.user.username as username,
            count(r) as totalRequests,
            count(distinct r.apiResource.id) as distinctResourcesUsed,
            avg(r.responseTimeMs) as averageResponseTimeMs
            FROM RequestLog r
                    WHERE r.requestTimestamp BETWEEN :fromDateTimeOffset AND :toDateTimeOffset AND r.user.username = :user
            GROUP BY r.user.username
            """)
    List<UserStatsDto> findUserAggregates(@Param("fromDateTimeOffset") OffsetDateTime from, @Param("toDateTimeOffset") OffsetDateTime to, @Param("user")String username);

}

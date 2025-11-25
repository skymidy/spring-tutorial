package com.tutorial.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@Entity
@Table(name = "request_logs")
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NonNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "api_resource_id", nullable = false)
    private ApiResource apiResource;

    @NonNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "authentication_type_id", nullable = false)
    private AuthenticationType authenticationType;

    @NonNull
    @Column(nullable = false, length = 10)
    private String httpMethod;

    @NonNull
    @Column(nullable = false, length = 2048)
    private String endpoint;

    @NonNull
    @Column(name = "request_timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime requestTimestamp = OffsetDateTime.now(ZoneOffset.UTC);

    @NonNull
    @Column
    private Integer responseStatus;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(length = 50)
    private String responseBodyType;

    @Column(name = "loaded_from_cache", nullable = false)
    private Boolean loadedFromCache = false;
}

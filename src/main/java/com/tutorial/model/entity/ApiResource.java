package com.tutorial.model.entity;

import org.hibernate.annotations.ColumnDefault;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "api_resources")
public class ApiResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "authentication_type_id", nullable = false)
    private AuthenticationType authenticationType;

    @NonNull
    @Column(nullable = false, length = 255)
    private String name;

    @NonNull
    @Column(nullable = false, length = 255)
    private String baseUrl;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isEnabled = true;

    @NonNull
    @Column(length = 32)
    private String apiKey;
}

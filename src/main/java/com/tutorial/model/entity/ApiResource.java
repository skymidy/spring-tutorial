package com.tutorial.model.entity;

import org.hibernate.annotations.ColumnDefault;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "api_resources")
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isEnabled = true;

    @NonNull
    @Column(length = 32)
    private String apiKey;

    @Column(length = 255, nullable = true)
    private String targetAuthHeader;
}

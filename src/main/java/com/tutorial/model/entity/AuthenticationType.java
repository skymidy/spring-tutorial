package com.tutorial.model.entity;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "authentication_types")
public class AuthenticationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(nullable = false, unique = true, length = 255)
    private String name;

}

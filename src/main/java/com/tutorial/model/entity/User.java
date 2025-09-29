package com.tutorial.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class User {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String apiKey;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("DEFAULT")
    private UserRoleEnum role;

    @ColumnDefault("0")
    private Long rateLimit;

}

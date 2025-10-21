package com.tutorial.model.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(nullable = false, unique = true, length = 255)
    @ColumnDefault("'USER'")
    private String name;

    @NonNull
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isAdmin = false;

}

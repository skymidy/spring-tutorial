package com.tutorial.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NonNull
    @Column(nullable = false)
    private String password;

    @NonNull
    @Column(unique = true, length = 255)
    private String apiKey;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Set<Authority> authorities;

    @Column
    @ColumnDefault("0")
    private Long rateLimit;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean enabled = true;

}

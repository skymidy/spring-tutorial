package com.tutorial.model.entity;

import org.hibernate.annotations.ColumnDefault;

import com.tutorial.Enum.UserRoleEnum;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private UserRoleEnum name = UserRoleEnum.DEFAULT;

    @NonNull
    @Column(name = "is_admin", nullable = false)
    @ColumnDefault("false")
    private Boolean isAdmin = false;

}

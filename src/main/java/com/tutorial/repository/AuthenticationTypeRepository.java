package com.tutorial.repository;

import com.tutorial.model.entity.AuthenticationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationTypeRepository extends JpaRepository<AuthenticationType, Integer> {
    Optional<AuthenticationType> findByName(String name);
}

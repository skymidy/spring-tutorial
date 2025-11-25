package com.tutorial.repository;

import com.tutorial.model.entity.AuthenticationType;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AuthenticationTypeRepository extends Repository<AuthenticationType, Integer> {
    AuthenticationType save(AuthenticationType type);

    Optional<AuthenticationType> findById(Integer id);
}

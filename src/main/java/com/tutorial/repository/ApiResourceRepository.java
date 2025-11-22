package com.tutorial.repository;

import com.tutorial.model.entity.ApiResource;

// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ApiResourceRepository extends JpaRepository<ApiResource, Integer> {

    Set<ApiResource> findAllByIsEnabledTrue();

    Set<ApiResource> findByOwner_Username(String username);

    Optional<ApiResource> findByNameAndIsEnabledTrue(String name);
    Optional<ApiResource> findByName(String name);

    void deleteById(Integer id);      // count deleted rows
    long deleteByName(String name);   // count deleted rows
}

package com.tutorial.repository;

import com.tutorial.model.entity.ApiResource;

// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ApiResourceRepository extends Repository<ApiResource, Integer> {
    ApiResource save(ApiResource resource);
    Optional<ApiResource> findById(Integer id);
    // @Query("SELECT a FROM ApiResource a WHERE a.name = ?1")
    // Optional<ApiResource> findByName(String name);
}

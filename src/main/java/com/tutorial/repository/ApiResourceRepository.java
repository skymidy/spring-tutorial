package com.tutorial.repository;

import com.tutorial.model.entity.ApiResource;

// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ApiResourceRepository extends Repository<ApiResource, Integer> {
    ApiResource save(ApiResource resource);
    Optional<ApiResource> findById(Integer id);

    List<ApiResource> findAll();

    List<ApiResource> findByOwner_Username(String username);

    void deleteById(Integer id);
}

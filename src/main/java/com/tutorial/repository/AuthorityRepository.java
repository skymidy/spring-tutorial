package com.tutorial.repository;

import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.AuthorityId;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AuthorityRepository extends Repository<Authority, AuthorityId> {

  Authority save(Authority role);

  Optional<Authority> findByUsername(String username);

  Optional<Authority> findByAuthority(String authority);
}

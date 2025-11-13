package com.tutorial.repository;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.AuthorityId;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorityRepository extends Repository<Authority, AuthorityId> {

    Authority save(Authority role);

    Optional<Authority> findByUsername(String username);

    Optional<Authority> findByAuthority(String authority);

    @Modifying
    @Query("INSERT INTO Authority (username, authority) VALUES (:username, :authority)")
    void addAuthorityToUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query("DELETE FROM Authority WHERE username = :username AND authority = :authority")
    void removeAuthorityFromUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query("DELETE FROM Authority WHERE username = :username")
    void removeAllAuthoritiesFromUser(@Param("username") String username);
}

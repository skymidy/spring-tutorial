package com.tutorial.repository;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.AuthorityId;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends Repository<Authority, AuthorityId> {

    Authority save(Authority role);

    Optional<Authority> findByUsername(String username);

    @Query("SELECT authority FROM Authority WHERE username = :username")
    List<String> findAllByUsername(@Param("username") String username);

    Optional<Authority> findByAuthority(String authority);

    @Modifying
    @Query("INSERT INTO Authority (username, authority) VALUES (:username, :authority)")
    int addAuthorityToUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query("DELETE FROM Authority WHERE username = :username AND authority = :authority")
    int removeAuthorityFromUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query("DELETE FROM Authority WHERE username = :username")
    int removeAllAuthoritiesFromUser(@Param("username") String username);
}

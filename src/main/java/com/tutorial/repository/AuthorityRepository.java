package com.tutorial.repository;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.AuthorityId;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface AuthorityRepository extends Repository<Authority, AuthorityId> {

    Authority save(Authority authority);

    Set<Authority> saveAll(Set<Authority> authority);

    Optional<Authority> findByUsername(String username);

    @Query("SELECT authority FROM Authority WHERE username = :username")
    Set<String> findAllByUsername(@Param("username") String username);

    Optional<Authority> findByAuthority(String authority);

    @Modifying
    @Query("INSERT INTO Authority (username, authority) VALUES (:username, :authority)")
    int addAuthorityToUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query("DELETE FROM Authority WHERE username = :username AND authority = :authority")
    int removeAuthorityFromUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query(value = "DELETE FROM Authority WHERE username = :username AND authority IN :authorities", nativeQuery = true)
    int removeAuthoritiesFromUser(@Param("username") String username, @Param("authorities") Set<AuthorityEnum> authorities);

    @Modifying
    @Query("DELETE FROM Authority WHERE username = :username")
    int removeAllAuthoritiesFromUser(@Param("username") String username);
}

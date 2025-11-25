package com.tutorial.repository;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.AuthorityId;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {

    Optional<Authority> findByUsername(String username);

    @Query("SELECT a.authority FROM Authority a WHERE a.username = :username")
    Set<AuthorityEnum> findAllByUsername(@Param("username") String username);

    Optional<Authority> findByAuthority(String authority);


    @Modifying
    @Query(value = "INSERT INTO authorities (username, authority) VALUES (:username, :authority)", nativeQuery = true)
    int addAuthorityToUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query("DELETE FROM Authority a WHERE a.username = :username AND a.authority = :authority")
    int removeAuthorityFromUser(@Param("username") String username, @Param("authority") AuthorityEnum authority);

    @Modifying
    @Query(value = "DELETE FROM authorities WHERE username = :username AND authority IN (:authorities)", nativeQuery = true)
    int removeAuthoritiesFromUser(@Param("username") String username, @Param("authorities") Set<AuthorityEnum> authorities);

    @Modifying
    @Query("DELETE FROM Authority a WHERE a.username = :username")
    int removeAllAuthoritiesFromUser(@Param("username") String username);
}

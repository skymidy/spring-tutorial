package com.tutorial.repository;

import com.tutorial.model.entity.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    User save(User user);

    Optional<User> findById(long id);
}

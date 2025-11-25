package com.tutorial.repository;

import com.tutorial.model.entity.RequestLog;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RequestLogRepository extends Repository<RequestLog, Integer> {
    RequestLog save(RequestLog log);

    Optional<RequestLog> findById(Integer id);
}

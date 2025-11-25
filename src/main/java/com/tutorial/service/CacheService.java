package com.tutorial.service;

import com.tutorial.model.dto.CacheDto;
import com.tutorial.model.dto.CachedHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Properties;

@Service
@Slf4j
public class CacheService {

    private final ReactiveRedisTemplate<String, CachedHttpResponse> reactiveRedisTemplate;
    private final ReactiveRedisConnectionFactory connectionFactory;

    public CacheService(
            @Qualifier("cachedResponseTemplate")
            ReactiveRedisTemplate<String, CachedHttpResponse> reactiveRedisTemplate,
            ReactiveRedisConnectionFactory connectionFactory) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.connectionFactory = connectionFactory;
    }

    public Mono<CacheDto> clearCache(){
        return reactiveRedisTemplate.keys("cache:*")
                .map(reactiveRedisTemplate::delete)
                .then(getRedisMemoryUsage());
    }

    public Mono<CacheDto> getRedisMemoryUsage() {
        return Mono.usingWhen(
                Mono.fromSupplier(connectionFactory::getReactiveConnection),
                connection -> connection.serverCommands()
                        .info()
                        .map(this::parseMemoryFromInfo),
                connection -> Mono.fromRunnable(connection::close)
        );
    }

    private CacheDto parseMemoryFromInfo(Properties info) {
        String value = info.getProperty("used_memory");

        if (value == null) {
            throw new RuntimeException("used_memory not found in Redis INFO response");
        }

        try {
            long bytes = Long.parseLong(value);
            return formatBytes(bytes);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to parse used_memory: " + value, e);
        }
    }

    private CacheDto formatBytes(long bytes) {
        if (bytes >= 1024 * 1024 * 1024) {
            return new CacheDto(bytes / (1024 * 1024 * 1024), "GB");
        } else if (bytes >= 1024 * 1024) {
            return new CacheDto(bytes / (1024 * 1024), "MB");
        } else if (bytes >= 1024) {
            return new CacheDto(bytes / 1024, "KB");
        }
        return new CacheDto(bytes, "bytes");
    }
}

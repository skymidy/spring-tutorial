package com.tutorial.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "hibernate.jdbc")
@Getter
@AllArgsConstructor
public class DBProperties {
    private final int batch_size;
}

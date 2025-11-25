package com.tutorial.configs;

import com.tutorial.Enum.AuthorityEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Configuration
@ConfigurationProperties(prefix = "app.temp-admin")
@Data
public class AdminProperties {
    private String username;
    private String password;
    private Set<AuthorityEnum> authorities = Set.of(AuthorityEnum.ADMIN);
}

package com.tutorial.configs;

import com.tutorial.Enum.AuthorityEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configuration
@ConfigurationProperties(prefix = "app.temp-admin")
@Data
public class AdminProperties {
    private String username;
    private String password;
    private List<AuthorityEnum> authorities = List.of(AuthorityEnum.ADMIN);
}

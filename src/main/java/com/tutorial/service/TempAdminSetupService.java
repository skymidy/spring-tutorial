package com.tutorial.service;

import com.tutorial.configs.AdminProperties;
import com.tutorial.mapper.AuthorityMapper;
import com.tutorial.model.dto.AuthorityDto;
import com.tutorial.model.entity.User;
import com.tutorial.repository.AuthorityRepository;
import com.tutorial.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TempAdminSetupService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;
    private final AuthorityMapper authorityMapper;

    public TempAdminSetupService(UserRepository userRepository,
                                 AuthorityRepository authorityRepository,
                                 PasswordEncoder passwordEncoder,
                                 AdminProperties adminProperties, AuthorityMapper authorityMapper) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
        this.authorityMapper = authorityMapper;
    }


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void setupTempAdmin() {
        if (adminProperties.getUsername() != null &&
                adminProperties.getPassword() != null) {

            Optional<User> existingUser = userRepository.findByUsername(adminProperties.getUsername());

            if (existingUser.isEmpty()) {
                createUser();
            } else {
                //TODO: Throw error to global handler
                log.info("Temporary admin user already exists: {}", existingUser.get().getUsername());
            }
        }
    }

    @Transactional
    private void createUser() {

        User tempAdmin = new User(adminProperties.getUsername(), passwordEncoder.encode(adminProperties.getPassword()));

        userRepository.save(tempAdmin);

        authorityRepository.saveAll(
                authorityMapper.toEntitySet(
                        tempAdmin.getUsername(),
                        new AuthorityDto(adminProperties.getAuthorities().stream().map(Enum::toString).collect(Collectors.toSet()))));

        log.info("Temporary admin created: {} with authority: {})",
                adminProperties.getUsername(), adminProperties.getAuthorities().toString());
    }
}

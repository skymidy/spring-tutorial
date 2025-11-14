package com.tutorial.service;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.configs.AdminProperties;
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

@Service
@Slf4j
public class TempAdminSetupService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public TempAdminSetupService(UserRepository userRepository,
                                 AuthorityRepository authorityRepository,
                                 PasswordEncoder passwordEncoder,
                                 AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
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

        for (AuthorityEnum authority : adminProperties.getAuthorities()) {
            authorityRepository.addAuthorityToUser(adminProperties.getUsername(),authority);
        }

        log.info("Temporary admin created: {} with authority: {})",
                adminProperties.getUsername(), adminProperties.getAuthorities().toString());
    }
}

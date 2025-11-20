package com.tutorial.service;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.RegistrationServiceException;
import com.tutorial.mapper.UserMapper;
import com.tutorial.model.dto.RegistrationRequestDto;
import com.tutorial.model.dto.UserDto;
import com.tutorial.model.entity.User;
import com.tutorial.repository.AuthorityRepository;
import com.tutorial.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyService apiKeyService;

    public RegistrationService(UserRepository userRepository, AuthorityRepository authorityRepository,
                               PasswordEncoder passwordEncoder, UserMapper userMapper, ApiKeyService apiKeyService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.apiKeyService = apiKeyService;
    }

    @Transactional
    public UserDto register(RegistrationRequestDto req) {

        validateRegistrationRequest(req);

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RegistrationServiceException(ErrorCodesEnum.USERNAME_ALREADY_EXISTS, ErrorCodesEnum.USERNAME_ALREADY_EXISTS.getMessage());
        }

        User saved = userRepository.save(
                new User(req.getUsername(), passwordEncoder.encode(req.getPassword()))
        );

        authorityRepository.addAuthorityToUser(saved.getUsername(), AuthorityEnum.USER);

        apiKeyService.generateApiKeyForUser(saved.getUsername());

        return userMapper.toDto(saved);
    }

    private void validateRegistrationRequest(RegistrationRequestDto req) {
        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) {
            throw new RegistrationServiceException(ErrorCodesEnum.USERNAME_EMPTY);
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new RegistrationServiceException(ErrorCodesEnum.PASSWORD_EMPTY);
        }
    }

}

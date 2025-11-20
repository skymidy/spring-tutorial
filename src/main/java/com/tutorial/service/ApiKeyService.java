package com.tutorial.service;

import java.security.SecureRandom;
import java.util.Optional;

import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.ApiKeyServiceException;
import com.tutorial.mapper.UserMapper;
import com.tutorial.model.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tutorial.model.entity.User;
import com.tutorial.repository.UserRepository;

@Service
@Transactional
public class ApiKeyService {


    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final int API_KEY_LENGTH = 255; //TODO: Magic value
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ApiKeyService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;

    }

    public String generateApiKeyForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        String newApiKey = generateUniqueApiKey();
        user.setApiKey(newApiKey);
        userRepository.save(user);

        return newApiKey;
    }

    public String regenerateApiKey(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        String newApiKey = generateUniqueApiKey();

        user.setApiKey(newApiKey);
        userRepository.save(user);

        return newApiKey;
    }

    public boolean validateApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey).isPresent();
    }

    public Optional<UserDto> getUserByApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey).map(userMapper::toDto);
    }

    private String generateUniqueApiKey() {
        int maxAttempts = 100; // Reasonable limit to prevent infinite loop

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String apiKey = generateApiKey();

            if (!userRepository.existsByApiKey(apiKey)) {
                return apiKey;
            }
        }

        throw new ApiKeyServiceException(ErrorCodesEnum.UNLUCKY_ERROR,"Failed to generate unique API key after " + maxAttempts + " attempts");
    }

    private String generateApiKey() {
        StringBuilder stringBuilder = new StringBuilder(API_KEY_LENGTH);

        for (int i = 0; i < API_KEY_LENGTH; i++) {
            stringBuilder.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
        }

        return stringBuilder.toString();
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiKeyServiceException(ErrorCodesEnum.USER_NOT_FOUND));
    }

    private boolean validateApiKey(String apiKey) {
        return userRepository.existsByApiKey(apiKey);
    }
}
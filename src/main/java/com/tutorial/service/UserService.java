package com.tutorial.service;

import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.UserServiceException;
import com.tutorial.mapper.UserMapper;
import com.tutorial.model.dto.UserDto;
import com.tutorial.model.entity.User;
import com.tutorial.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true) // Keeps session open
    public UserDto findUserDtoByApiKey(String apiKey) {
        User user = userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new UserServiceException(ErrorCodesEnum.USER_NOT_FOUND));
        return userMapper.toDto(user);

    }
}

package com.tutorial.service;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.PasswordServiceException;
import com.tutorial.mapper.UserMapper;
import com.tutorial.model.dto.PasswordDto;
import com.tutorial.model.dto.UserDto;
import com.tutorial.model.entity.User;
import com.tutorial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Update user password.
     * TODO: Could be faster with optimised SQL queries in repository
     */
    @Transactional
    public UserDto updatePassword(String username, PasswordDto passwordDto) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new PasswordServiceException(ErrorCodesEnum.USER_NOT_FOUND)
                );

        if(passwordEncoder.matches(passwordDto.getPassword(), user.getPassword())) {
            throw new PasswordServiceException(ErrorCodesEnum.UNACCEPTABLE_PASSWORD, "New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        User saved = userRepository.save(user);

        log.info("Updated user password username={}", saved.getUsername());
        return userMapper.toDto(saved);
    }
}

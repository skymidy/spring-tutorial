package com.tutorial.service;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.PasswordServiceException;
import com.tutorial.mapper.UserMapper;
import com.tutorial.model.dto.UserDto;
import com.tutorial.model.entity.User;
import com.tutorial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

    private static final AuthorityEnum ADMIN_AUTHORITY = AuthorityEnum.ADMIN;

    public UserDto updateCurrentUserPassword(String newPassword, UserDetails currentUser){
        return updatePassword(currentUser.getUsername(), newPassword, currentUser);
    }

    /**
     * Update user password.
     * TODO: Could be faster with optimised SQL queries in repository
     */
    @Transactional
    public UserDto updatePassword(String username, String newPassword, UserDetails currentUser) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new PasswordServiceException(ErrorCodesEnum.USER_NOT_FOUND)
                );

        if (isTargetUserSameOrAdmin(username, currentUser)
        ) {
            log.warn("Non-admin attempted to update user id={}", username);
            throw new PasswordServiceException(ErrorCodesEnum.ACCESS_DENIED);
        }

        if(passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordServiceException(ErrorCodesEnum.UNACCEPTABLE_PASSWORD, "New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        User saved = userRepository.save(user);

        log.info("Updated user password username={}", saved.getUsername());
        return userMapper.toDto(saved);
    }

    private static boolean isTargetUserSameOrAdmin(String username, UserDetails currentUser) {
        return !currentUser
                .getAuthorities()
                .contains(
                        new SimpleGrantedAuthority(AuthorityEnum.ADMIN.toString())
                )
                ||
                !currentUser.getUsername().equalsIgnoreCase(username);
    }
}

package com.tutorial.service;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.configs.DBProperties;
import com.tutorial.exceptions.AuthorityServiceException;
import com.tutorial.mapper.AuthorityMapper;
import com.tutorial.model.dto.AuthorityDto;
import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.User;
import com.tutorial.repository.AuthorityRepository;
import com.tutorial.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorityService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper;
    private final DBProperties dbProperties;

    public AuthorityService(UserRepository userRepository, AuthorityRepository authorityRepository, AuthorityMapper authorityMapper, DBProperties dbProperties) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.authorityMapper = authorityMapper;
        this.dbProperties = dbProperties;
    }

    public Set<String> getAllAvailableAuthorities() {
        return Arrays.stream(AuthorityEnum.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    public Set<String> getAllUserAuthorities(String username) {
        usernameCheck(username);

        // repository returns AuthorityEnum values; convert to String names for API
        return authorityRepository.findAllByUsername(username).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }


    public Set<String> assignAuthorityToUser(String username, AuthorityDto authorityDto) {

        usernameCheck(username);

        authorityRepository.saveAll(getAuthorities(username, authorityDto));

        return authorityRepository.findAllByUsername(username).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    public Set<String> removeAuthorityFromUser(String username, AuthorityDto authorityDto) {
        usernameCheck(username);

        Set<AuthorityEnum> authorities = authorityDto.getAuthorities().stream().map(AuthorityEnum::valueOf).collect(Collectors.toSet());

        if (authorityRepository.removeAuthoritiesFromUser(username, authorities) == 0) {
            //TODO: Add custom error code
            throw new AuthorityServiceException(ErrorCodesEnum.DB_ERROR, ErrorCodesEnum.DB_ERROR.getMessage());
        }

        return authorityRepository.findAllByUsername(username).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    private Set<Authority> getAuthorities(String username, AuthorityDto authorityDto) {
        if (authorityDto.getAuthorities().size() > dbProperties.getBatch_size()) {
            throw new AuthorityServiceException(ErrorCodesEnum.UNACCEPTABLE_AUTHORITY,
                    "Too many authorities (%d) > batchSize(%d)"
                            .formatted(authorityDto.getAuthorities().size(), dbProperties.getBatch_size()));
        }

        try {
            return authorityMapper.toEntitySet(username, authorityDto);
        } catch (IllegalArgumentException e) {
            throw new AuthorityServiceException(ErrorCodesEnum.UNACCEPTABLE_AUTHORITY, ErrorCodesEnum.UNACCEPTABLE_AUTHORITY.getMessage());
        }
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthorityServiceException(ErrorCodesEnum.USER_NOT_FOUND));
    }

    private void usernameCheck(String username) {
        if (username == null || username.isEmpty()) {
            throw new AuthorityServiceException(ErrorCodesEnum.USERNAME_EMPTY, ErrorCodesEnum.USERNAME_EMPTY.getMessage());
        }
        userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthorityServiceException(ErrorCodesEnum.USER_NOT_FOUND, ErrorCodesEnum.USER_NOT_FOUND.getMessage()));
    }
}

package com.tutorial.service;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.AuthorityServiceException;
import com.tutorial.model.entity.User;
import com.tutorial.repository.AuthorityRepository;
import com.tutorial.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public AuthorityService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public List<AuthorityEnum> getAllAvailableAuthorities() {
        return List.of(AuthorityEnum.values());
    }

    public List<String> assignAuthorityToUser(String username, String authority) {
        if (username == null || username.isEmpty()) {
            throw new AuthorityServiceException(ErrorCodesEnum.USERNAME_EMPTY, ErrorCodesEnum.USERNAME_EMPTY.getMessage());
        }
        try{
            AuthorityEnum.valueOf(authority);
        }
        catch (IllegalArgumentException e){
            throw new AuthorityServiceException(ErrorCodesEnum.UNACCEPTABLE_AUTHORITY, ErrorCodesEnum.UNACCEPTABLE_AUTHORITY.getMessage());
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthorityServiceException(ErrorCodesEnum.USER_NOT_FOUND, ErrorCodesEnum.USER_NOT_FOUND.getMessage()));

        authorityRepository.addAuthorityToUser(username, AuthorityEnum.valueOf(authority));

        return authorityRepository.findAllByUsername(username);
    }

    public List<String> removeAuthorityFromUser(String username, String authority) {
        if (username == null || username.isEmpty()) {
            throw new AuthorityServiceException(ErrorCodesEnum.USERNAME_EMPTY, ErrorCodesEnum.USERNAME_EMPTY.getMessage());
        }
        try{
            AuthorityEnum.valueOf(authority);
        }
        catch (IllegalArgumentException e){
            throw new AuthorityServiceException(ErrorCodesEnum.UNACCEPTABLE_AUTHORITY, ErrorCodesEnum.UNACCEPTABLE_AUTHORITY.getMessage());
        }

        userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthorityServiceException(ErrorCodesEnum.USER_NOT_FOUND, ErrorCodesEnum.USER_NOT_FOUND.getMessage()));

        if(authorityRepository.removeAuthorityFromUser(username, AuthorityEnum.valueOf(authority)) == 0){
            //TODO: Add custom error code
            throw new AuthorityServiceException(ErrorCodesEnum.DB_ERROR, ErrorCodesEnum.DB_ERROR.getMessage());
        }

        return authorityRepository.findAllByUsername(username);
    }
}

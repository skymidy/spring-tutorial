package com.tutorial.service;

import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.RateLimitServiceException;
import com.tutorial.mapper.UserMapper;
import com.tutorial.model.dto.RateLimitDto;
import com.tutorial.model.dto.UserDto;
import com.tutorial.model.entity.User;
import com.tutorial.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public RateLimitService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto updateUserRateLimit(String username, Long rateLimit){

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RateLimitServiceException(ErrorCodesEnum.USER_NOT_FOUND));

        if(rateLimit <= 0) {
            throw new RateLimitServiceException(ErrorCodesEnum.UNACCEPTABLE_RATE_LIMIT_VALUE, "RateLimit value zer or below unacceptable");
        }

        user.setRateLimit(rateLimit);

        return userMapper.toDto(userRepository.save(user));
    }

    public RateLimitDto getUserRateLimit(String username) {
        return new RateLimitDto(
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new RateLimitServiceException(ErrorCodesEnum.USER_NOT_FOUND))
                        .getRateLimit()
        );
    }
}

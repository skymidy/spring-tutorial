package com.tutorial.mapper;

import org.mapstruct.*;

import com.tutorial.model.dto.UserDto;
import com.tutorial.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserDto dto);

}

package com.tutorial.mapper;

import com.tutorial.model.entity.Authority;
import org.mapstruct.*;

import com.tutorial.model.dto.UserDto;
import com.tutorial.model.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserDto dto);

    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "mapAuthorities")
    UserDto toDto(User user);

    @Named("mapAuthorities")
    default Set<String> mapAuthorities(Set<Authority> authorities) {
        if (authorities == null) {
            return null;
        }
        return authorities.stream()
                .map(Authority::getAuthority)
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

}

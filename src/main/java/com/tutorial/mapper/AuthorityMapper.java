package com.tutorial.mapper;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.model.dto.AuthoritiesDto;
import com.tutorial.model.entity.Authority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    default AuthorityEnum toEnum(String authority) {
        return AuthorityEnum.valueOf(authority);
    }

    @Mapping(target = "username", source = "username")
    @Mapping(target = "authority", source = "authority")
    Authority toEntity(String authority, String username);



    default Set<Authority> toEntitySet(String username, AuthoritiesDto dto) {
        if (dto == null || dto.getAuthorities() == null) {
            return Set.of();
        }

        return dto.getAuthorities().stream()
                .map(auth -> toEntity(auth, username))
                .collect(Collectors.toSet());
    }
}

package com.tutorial.mapper;

import org.mapstruct.*;
import com.tutorial.model.entity.User;
import com.tutorial.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "role.id", target = "roleId")
  @Mapping(source = "role.name", target = "roleName")
  UserDto toDto(User user);

  @Mapping(source = "roleId", target = "role.id")
  @Mapping(target = "password", ignore = true)
  User toEntity(UserDto dto);
}

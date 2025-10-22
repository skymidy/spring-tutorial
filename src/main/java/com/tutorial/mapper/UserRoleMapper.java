package com.tutorial.mapper;

import org.mapstruct.Mapper;
import com.tutorial.model.entity.UserRole;
import com.tutorial.dto.UserRoleDto;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
  UserRoleDto toDto(UserRole role);

  UserRole toEntity(UserRoleDto dto);
}

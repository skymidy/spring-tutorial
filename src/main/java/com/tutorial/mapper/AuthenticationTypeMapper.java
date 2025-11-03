package com.tutorial.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tutorial.model.dto.AuthenticationTypeDto;
import com.tutorial.model.entity.AuthenticationType;

@Mapper(componentModel = "spring")
public interface AuthenticationTypeMapper {
  AuthenticationTypeDto toDto(AuthenticationType entity);
  @Mapping(target = "id", ignore = true)
  AuthenticationType toEntity(AuthenticationTypeDto dto);
}

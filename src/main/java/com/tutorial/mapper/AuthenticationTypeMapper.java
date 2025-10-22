package com.tutorial.mapper;

import org.mapstruct.Mapper;
import com.tutorial.model.entity.AuthenticationType;
import com.tutorial.dto.AuthenticationTypeDto;

@Mapper(componentModel = "spring")
public interface AuthenticationTypeMapper {
  AuthenticationTypeDto toDto(AuthenticationType entity);

  AuthenticationType toEntity(AuthenticationTypeDto dto);
}

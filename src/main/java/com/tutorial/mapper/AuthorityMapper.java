package com.tutorial.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tutorial.model.dto.AuthorityDto;
import com.tutorial.model.entity.Authority;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

  AuthorityDto toDto(Authority role);

  Authority toEntity(AuthorityDto dto);
}

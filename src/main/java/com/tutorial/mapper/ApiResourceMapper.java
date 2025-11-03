package com.tutorial.mapper;

import org.mapstruct.*;
import org.mapstruct.Mapping;

import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.entity.ApiResource;

@Mapper(componentModel = "spring")
public interface ApiResourceMapper {

  @Mapping(source = "authenticationType.id", target = "authenticationTypeId")
  @Mapping(source = "authenticationType.name", target = "authenticationTypeName")
  ApiResourceDto toDto(ApiResource entity);

  @Mapping(source = "authenticationTypeId", target = "authenticationType.id")
  @Mapping(target = "id", ignore = true)
  ApiResource toEntity(ApiResourceDto dto);
}

package com.tutorial.mapper;

import org.mapstruct.*;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.dto.ApiResourceDto;

@Mapper(componentModel = "spring")
public interface ApiResourceMapper {

  @Mapping(source = "authenticationType.id", target = "authenticationTypeId")
  @Mapping(source = "authenticationType.name", target = "authenticationTypeName")
  ApiResourceDto toDto(ApiResource entity);

  @Mapping(source = "authenticationTypeId", target = "authenticationType.id")
  ApiResource toEntity(ApiResourceDto dto);
}

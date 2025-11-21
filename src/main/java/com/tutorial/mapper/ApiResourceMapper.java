package com.tutorial.mapper;

import org.mapstruct.*;
import org.mapstruct.Mapping;

import com.tutorial.model.dto.ApiResourceDto;
import com.tutorial.model.entity.ApiResource;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ApiResourceMapper {

  @Mapping(source = "authenticationType.id", target = "authenticationTypeId")
  @Mapping(source = "authenticationType.name", target = "authenticationTypeName")
  ApiResourceDto toDto(ApiResource entity);

  @Mapping(target = "owner", ignore = true)
  @Mapping(source = "authenticationTypeId", target = "authenticationType.id")
  @Mapping(target = "id", ignore = true)
  ApiResource toEntity(ApiResourceDto dto);

  default Set<ApiResourceDto> toDtoSet(Collection<ApiResource> entities){
      return entities.stream().map(this::toDto).collect(Collectors.toSet());
  }
}

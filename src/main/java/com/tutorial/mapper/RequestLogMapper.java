package com.tutorial.mapper;

import org.mapstruct.*;
import com.tutorial.model.entity.RequestLog;
import com.tutorial.dto.RequestLogDto;

@Mapper(componentModel = "spring")
public interface RequestLogMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "apiResource.id", target = "apiResourceId")
  @Mapping(source = "authenticationType.id", target = "authenticationTypeId")
  @Mapping(source = "responseTimeMs", target = "responseTimeMs")
  RequestLogDto toDto(RequestLog entity);

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "apiResourceId", target = "apiResource.id")
  @Mapping(source = "authenticationTypeId", target = "authenticationType.id")
  @Mapping(source = "responseTimeMs", target = "responseTimeMs")
  RequestLog toEntity(RequestLogDto dto);
}

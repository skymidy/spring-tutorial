package com.tutorial.mapper;

import com.tutorial.model.dto.CachedHttpResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface HttpResponseMapper {
    HttpResponseMapper INSTANCE = Mappers.getMapper(HttpResponseMapper.class);

    default CachedHttpResponse toDto(ResponseEntity<byte[]> response) {
        if (response == null) return null;

        CachedHttpResponse dto = new CachedHttpResponse();
        dto.setStatusCode(response.getStatusCodeValue());
        dto.setBody(response.getBody());

        MultiValueMap<String, String> headers = response.getHeaders();
        Map<String, List<String>> serializableHeaders = new HashMap<>();
        headers.forEach((key, values) -> serializableHeaders.put(key, new ArrayList<>(values)));
        dto.setHeaders(serializableHeaders);

        return dto;
    }

    default ResponseEntity<byte[]> toResponseEntity(CachedHttpResponse dto) {
        if (dto == null) return null;

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(dto.getStatusCode());
        if (dto.getHeaders() != null) {
            dto.getHeaders().forEach((key, values) -> {
                builder.header(key, values.toArray(new String[0]));
            });
        }

        return builder.body(dto.getBody() != null ? dto.getBody() : new byte[0]);
    }
}

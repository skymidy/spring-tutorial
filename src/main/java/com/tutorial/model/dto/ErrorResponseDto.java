package com.tutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private int statusCode;
    private String message;
}

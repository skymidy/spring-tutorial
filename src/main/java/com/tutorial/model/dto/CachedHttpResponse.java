package com.tutorial.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class CachedHttpResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    // Getters and setters
    private int statusCode;
    private Map<String, List<String>> headers;
    private byte[] body;

}

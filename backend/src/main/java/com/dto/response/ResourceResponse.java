package com.dto.response;

import com.entity.ResourceType;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public record ResourceResponse(String path,
                               String name,
                               @JsonInclude(NON_NULL) Long size,
                               ResourceType type) {
}

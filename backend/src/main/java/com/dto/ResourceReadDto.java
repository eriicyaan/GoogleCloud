package com.dto;

import com.entity.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceReadDto {
    private String path;
    private String name;
    private Long size;
    private ResourceType type;
}

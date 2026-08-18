package com.dto;

import com.validation.ValidPath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourcePathDto {

    @ValidPath
    String path;
}

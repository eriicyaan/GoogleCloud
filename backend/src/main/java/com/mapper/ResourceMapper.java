package com.mapper;

import com.dto.ResourceReadDto;
import com.entity.ResourceType;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ResourceMapper implements Mapper<StatObjectResponse, ResourceReadDto> {



    @Override
    public ResourceReadDto map(StatObjectResponse object) {
        return null;
    }


    public ResourceReadDto map(StatObjectResponse object, Integer prefixLength) {
        String path = getPath(object.object(), prefixLength);

        List<String> params = getParams(path, String.valueOf(object.size()));

        return ResourceReadDto.builder()
                .path(params.get(0))
                .name(params.get(1))
                .size(params.get(2) == null ? null : Long.valueOf(params.get(2)))
                .type(ResourceType.valueOf(params.get(3)))
                .build();
    }

    public ResourceReadDto map(Item item, Integer prefixLength) {
        String path = getPath(item.objectName(), prefixLength);

        List<String> params = getParams(path, String.valueOf(item.size()));

        return ResourceReadDto.builder()
                .path(params.get(0))
                .name(params.get(1))
                .size(params.get(2) == null ? null : Long.valueOf(params.get(2)))
                .type(ResourceType.valueOf(params.get(3)))
                .build();
    }


    private List<String> getParams(String path, String size) {
        boolean condition = path.endsWith("/");

        ArrayList<String> list = new ArrayList<>(List.of(path.split("/")));

        String folder = path;
        String file = "";

        if(!condition) {
            file = list.get(list.size() - 1);
            list.remove(list.size() - 1);
            folder = String.join("/", list);
        }
        String resSize = condition ? null : size;

        ResourceType type = condition ? ResourceType.DIRECTORY : ResourceType.FILE;

        return Arrays.asList(folder, file, resSize, type.name());
    }

    private String getPath(String absolutePath, Integer prefixLength) {
        return absolutePath.substring(prefixLength);
    }
}

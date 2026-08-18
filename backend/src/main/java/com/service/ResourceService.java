package com.service;


import com.dto.ResourceReadDto;
import com.dto.UserReadDto;
import com.exception.DirectoryNotExistsException;
import com.mapper.ResourceMapper;

import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final MinIOService minIOService;
    private final UserService userService;
    private final ResourceMapper resourceMapper;
    private final String bucket = "user-files";

    public ResourceReadDto getResource(String path, String username) {

        UserReadDto user = userService.findByUsername(username);

        String prefix = "user-" + user.getId() + "-files";
        String object = String.join("/", prefix, path).replaceAll("//", "/");

        return Optional.of(minIOService.getResource(bucket, object))
                .map(resource-> resourceMapper.map(resource, prefix.length()))
                .orElseThrow();
    }

    public void deleteResource(String path, String username) {
        UserReadDto user = userService.findByUsername(username);

        String prefix = "user-" + user.getId() + "-files";
        String object = String.join("/", prefix, path);

        minIOService.deleteResource(bucket, object);
    }

    public byte[] downloadResource(String path, String username) {
        UserReadDto user = userService.findByUsername(username);

        String prefix = "user-" + user.getId() + "-files";
        String object = String.join("/", prefix, path);

        return minIOService.downloadResource(bucket, object);
    }

    public ResourceReadDto moveOrRenameResource(String fromPath, String toPath, String username) {
        UserReadDto user = userService.findByUsername(username);

        String prefix = "user-" + user.getId() + "-files";
        String from = String.join("/", prefix, fromPath).replaceAll("//", "/");
        String to = String.join("/", prefix, toPath).replaceAll("//", "/");

        minIOService.moveOrRenameResource(bucket, from, to);

        return getResource(toPath, username);
    }


    public List<ResourceReadDto> searchResource(String query, String username, boolean recursive) {
        try {
            UserReadDto user = userService.findByUsername(username);

            String prefix = "user-" + user.getId() + "-files";

            Iterable<Result<Item>> allResources = minIOService.searchResources(bucket, prefix, recursive);

            List<Item> result = new ArrayList<>();


            for (Result<Item> item: allResources) {
                String objectName = item.get().objectName();
                String path = objectName.substring(prefix.length());

                if (path.contains(query)) {
                    result.add(item.get());
                }
            }

            return result.stream()
                    .map(item -> resourceMapper.map(item, prefix.length()))
                    .toList();

        } catch (MinioException e) {
            throw new RuntimeException();
        }
    }


    public ResourceReadDto uploadResource(String path, MultipartFile file, String username) {
        UserReadDto user = userService.findByUsername(username);

        String prefix = "user-" + user.getId() + "-files";
        String fullPath = String.join("/", prefix, path, file.getOriginalFilename()).replaceAll("//", "/");


        return Optional.of(minIOService.uploadResource(bucket, fullPath, file))
                .map(resource-> resourceMapper.map(resource, prefix.length()))
                .orElseThrow();
    }
}

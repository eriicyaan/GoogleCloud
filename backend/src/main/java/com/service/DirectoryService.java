package com.service;

import com.dto.ResourceReadDto;
import com.dto.UserReadDto;
import com.exception.DirectoryIsAlreadyExistsException;
import com.exception.DirectoryNotExistsException;
import com.exception.ParentDirectoryNotExistsException;
import com.mapper.ResourceMapper;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final UserService userService;
    private final MinIOService minIOService;
    private final String bucket = "user-files";
    private final ResourceMapper resourceMapper;

    public List<ResourceReadDto> getResources(String path, String username, boolean recursive) {
        try {
            UserReadDto user = userService.findByUsername(username);

            String prefix = "user-" + user.getId() + "-files";
            String fullPrefix = String.join("/", prefix, path);

            List<Item> result = new ArrayList<>();

            boolean directoryExists = isDirectoryExists(fullPrefix);

            if(!directoryExists) {
                throw new DirectoryNotExistsException("directory not exists");
            }

            Iterable<Result<Item>> allResources = minIOService.searchResources(bucket, fullPrefix, recursive);


            for(Result<Item> resource : allResources) {
                result.add(resource.get());
            }

            return result.stream()
                    .map(item -> resourceMapper.map(item, prefix.length()))
                    .toList();

        } catch (MinioException e) {
            throw new RuntimeException();
        }
    }


    public ResourceReadDto createDirectory(String path, String username) {
        UserReadDto user = userService.findByUsername(username);

        String prefix = "user-" + user.getId() + "-files";
        String fullPath = String.join("/", prefix, path);

        boolean isParentDirectoryExists = isParentDirectoryExists(fullPath);
        boolean isDirectoryExists = isDirectoryExists(fullPath);

        if(!isParentDirectoryExists) {
            throw new ParentDirectoryNotExistsException("parent directory not exists");
        }

        if(isDirectoryExists) {
            throw new DirectoryIsAlreadyExistsException("directory is already exists");
        }

        return Optional.of(minIOService.createDirectory(bucket, fullPath))
                .map(resource -> resourceMapper.map(resource, prefix.length()))
                .orElseThrow();
    }

    private boolean isParentDirectoryExists(String fullPath) {
        List<String> path = new ArrayList<>(Arrays.asList(fullPath.split("/")));

        path.remove(path.size() - 1);

        if(path.size() == 1) {
            String str = path.get(0);

            if(str.startsWith("user-") && str.endsWith("-files")) return true;
        }

        Iterable<Result<Item>> results = minIOService.searchResources(bucket, String.join("/", path), true);

        for(Result<Item> item: results) {
            return true;
        }
        return false;
    }


    private boolean isDirectoryExists(String path) {
        Iterable<Result<Item>> items = minIOService.searchResources(bucket, path, false);

        for(Result<Item> item: items) {
            return true;
        }
        return false;
    }
}

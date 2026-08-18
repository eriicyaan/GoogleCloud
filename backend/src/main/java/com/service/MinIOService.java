package com.service;

import com.exception.ResourceExistsException;
import com.exception.ResourceNotFoundException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class MinIOService {

    private final MinioClient minioClient;

    public StatObjectResponse getResource(String bucketName, String path) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()
            );
        } catch (MinioException e) {
            throw new ResourceNotFoundException("resource not found");
        }
    }

    public void deleteResource(String bucket, String path) {
        try {
            getResource(bucket, path);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (MinioException e) {
            throw new ResourceNotFoundException("resource not found");
        }
    }

    public byte[] downloadResource(String bucket, String path) {
        try(InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .build())) {
            getResource(bucket, path);
            return inputStream.readAllBytes();

        } catch (IOException | MinioException e) {
            throw new ResourceNotFoundException("resource not found");
        }
    }


    public void moveOrRenameResource(String bucket, String fromPath, String toPath) {
        try {

            getResource(bucket, fromPath);

            try {
                getResource(bucket, toPath);
                throw new ResourceExistsException("resource in path: " + toPath + " exists");
            } catch (Exception e) {

            }
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(toPath)
                            .source(
                                    SourceObject.builder()
                                            .bucket(bucket)
                                            .object(fromPath)
                                            .build()
                            )
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fromPath)
                            .build()
            );
        } catch (MinioException e) {
            throw new ResourceNotFoundException("resource not found");
        }
    }

    public Iterable<Result<Item>> searchResources(String bucket, String prefix, boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(prefix)
                        .recursive(recursive)
                        .build()
        );
    }

    public StatObjectResponse uploadResource(String bucket, String fullPath, MultipartFile file) {
        if(isResourceExists(bucket, fullPath)) {
            throw new ResourceExistsException("resource is already exists");
        }

        try(InputStream inputStream = file.getInputStream()) {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fullPath)
                            .stream(inputStream, file.getSize(), -1L)
                            .data(file.getBytes(), file.getBytes().length)
                            .build());

            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(response.bucket())
                            .object(response.object())
                            .build());

        } catch (IOException |MinioException e) {
            throw new RuntimeException(e);
        }
    }

    public StatObjectResponse createDirectory(String bucket, String fullPath) {
        try {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fullPath)
                            .data(new byte[]{}, 0)
                            .build());

            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(response.bucket())
                            .object(response.object())
                            .build());

        } catch (MinioException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isResourceExists(String bucket, String path) {
        try {
            getResource(bucket, path);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}

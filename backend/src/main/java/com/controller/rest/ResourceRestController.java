package com.controller.rest;

import com.dto.ResourcePathDto;
import com.dto.ResourceReadDto;
import com.dto.response.ResourceResponse;
import com.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/resource")
@RequiredArgsConstructor
public class ResourceRestController {

    private final ResourceService resourceService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResourceResponse getResource(@RequestParam("path") @Validated ResourcePathDto resourcePath,
                                        @AuthenticationPrincipal UserDetails userDetails) {

        ResourceReadDto resource = resourceService.getResource(resourcePath.getPath(), userDetails.getUsername());

        return new ResourceResponse(
                resource.getPath(),
                resource.getName(),
                resource.getSize(),
                resource.getType()
        );
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@RequestParam("path") @Validated ResourcePathDto resourcePath,
                               @AuthenticationPrincipal UserDetails userDetails) {

        resourceService.deleteResource(resourcePath.getPath(), userDetails.getUsername());
    }


    @GetMapping( "/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> downloadResource(@RequestParam("path") @Validated ResourcePathDto resourcePath,
                                                   @AuthenticationPrincipal UserDetails userDetails) {

        String path = resourcePath.getPath();

        byte[] file = resourceService.downloadResource(path, userDetails.getUsername());
        String filename = path.substring(path.lastIndexOf("/") + 1);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @PostMapping("move")
    @ResponseStatus(HttpStatus.OK)
    public ResourceResponse moveOrRenameResource(@RequestParam("from") @Validated ResourcePathDto resourceFromPath,
                                                 @RequestParam("to") @Validated ResourcePathDto resourceToPath,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        ResourceReadDto resource = resourceService
                .moveOrRenameResource(
                        resourceFromPath.getPath(),
                        resourceToPath.getPath(),
                        userDetails.getUsername());

        return new ResourceResponse(
                resource.getPath(),
                resource.getName(),
                resource.getSize(),
                resource.getType()
        );
    }

    @GetMapping("search")
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceResponse> searchResource(@RequestParam("query") @Validated ResourcePathDto resourcePath,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        List<ResourceReadDto> resources = resourceService
                    .searchResource(resourcePath.getPath(), userDetails.getUsername(), true);


        return resources.stream()
                .map(resource ->
                        new ResourceResponse(resource.getPath(), resource.getName(),
                                            resource.getSize(),
                                            resource.getType())
                )
                .toList();
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse uploadResource(@RequestParam("path") @Validated ResourcePathDto resourcePath,
                                           @RequestParam MultipartFile file,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        ResourceReadDto resource = resourceService
                .uploadResource(resourcePath.getPath(), file, userDetails.getUsername());

        return new ResourceResponse(
                resource.getPath(),
                resource.getName(),
                resource.getSize(),
                resource.getType()
        );
    }

}

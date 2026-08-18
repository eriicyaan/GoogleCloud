package com.controller.rest;

import com.dto.ResourcePathDto;
import com.dto.ResourceReadDto;
import com.dto.response.ResourceResponse;
import com.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/directory")
@RequiredArgsConstructor
public class DirectoryRestController {
    private final DirectoryService directoryService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceResponse> getResources(@RequestParam("path") @Validated ResourcePathDto resourcePath,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        List<ResourceReadDto> resources= directoryService
                .getResources(resourcePath.getPath(), userDetails.getUsername(), false);

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
    public ResourceResponse createDirectory(@RequestParam("path") @Validated ResourcePathDto resourcePathDto,
                                            @AuthenticationPrincipal UserDetails userDetails) {

        ResourceReadDto resource = directoryService.createDirectory(resourcePathDto.getPath(), userDetails.getUsername());

        return new ResourceResponse(
                resource.getPath(),
                resource.getName(),
                resource.getSize(),
                resource.getType()
        );
    }
}

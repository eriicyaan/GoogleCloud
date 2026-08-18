package com.controller.rest;


import com.dto.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserRestController {


    @GetMapping("me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return new UserResponse(user.getUsername());
    }

}

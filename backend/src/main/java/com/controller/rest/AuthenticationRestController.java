package com.controller.rest;

import com.dto.*;
import com.dto.response.UserResponse;
import com.exception.UsernameAlreadyExistsException;
import com.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping("sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signUp(@RequestBody @Validated UserCreateEditDto user,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        UserReadDto createdUser = userService.create(user);
        createSession(user, request, response);

        return new UserResponse(createdUser.getUsername());
    }

    @PostMapping("sign-in")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse signIn(@RequestBody @Validated UserSignInDto user,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        createSession(user, request, response);

        return new UserResponse(user.getUsername());
    }

    @PostMapping("sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private void createSession(Object user, HttpServletRequest request, HttpServletResponse response) {

        boolean flag = user instanceof UserSignInDto;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        flag
                                ? ((UserSignInDto) user).getUsername()
                                : ((UserCreateEditDto) user).getUsername(),
                        flag
                                ? ((UserSignInDto) user).getPassword()
                                : ((UserCreateEditDto) user).getPassword()
                )
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);

        securityContextRepository.saveContext(
                SecurityContextHolder.getContext(),
                request,
                response
        );
    }
}

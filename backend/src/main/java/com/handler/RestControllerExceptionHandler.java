package com.handler;


import com.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(basePackages = "com.controller.rest")
@Slf4j
public class RestControllerExceptionHandler {

    @ExceptionHandler({UsernameAlreadyExistsException.class, ResourceExistsException.class, DirectoryIsAlreadyExistsException.class})
    public ProblemDetail handleUsernameException(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ProblemDetail handleValidationException(Exception exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ProblemDetail handleBadCredentialsException() {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "username or password is incorrect");

    }


    @ExceptionHandler({ResourceNotFoundException.class, DirectoryNotExistsException.class, ParentDirectoryNotExistsException.class})
    public ProblemDetail handleResourceNotFoundException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleServerException(Exception exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage());
    }
}

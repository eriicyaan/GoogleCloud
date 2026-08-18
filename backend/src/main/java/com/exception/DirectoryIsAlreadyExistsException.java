package com.exception;

public class DirectoryIsAlreadyExistsException extends RuntimeException {
    public DirectoryIsAlreadyExistsException(String message) {
        super(message);
    }
}

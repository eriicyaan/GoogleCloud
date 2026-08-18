package com.exception;

public class DirectoryNotExistsException extends RuntimeException {
    public DirectoryNotExistsException(String message) {
        super(message);
    }
}

package com.exception;

public class ParentDirectoryNotExistsException extends RuntimeException {
    public ParentDirectoryNotExistsException(String message) {
        super(message);
    }
}

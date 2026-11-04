package com.hirehub.exception;

public class RecruiterProfileAlreadyExistsException
        extends RuntimeException {

    public RecruiterProfileAlreadyExistsException(String message) {
        super(message);
    }
}
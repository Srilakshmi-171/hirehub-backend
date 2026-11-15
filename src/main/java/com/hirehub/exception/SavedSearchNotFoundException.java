package com.hirehub.exception;

public class SavedSearchNotFoundException extends RuntimeException {

    public SavedSearchNotFoundException(String message) {
        super(message);
    }
}
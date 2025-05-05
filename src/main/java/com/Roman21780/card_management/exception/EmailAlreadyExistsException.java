// EmailAlreadyExistsException.java
package com.Roman21780.card_management.exception;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
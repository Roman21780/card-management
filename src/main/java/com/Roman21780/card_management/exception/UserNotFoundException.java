// UserNotFoundException.java
package com.Roman21780.card_management.exception;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
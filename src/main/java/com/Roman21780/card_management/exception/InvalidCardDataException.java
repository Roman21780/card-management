// InvalidCardDataException.java
package com.Roman21780.card_management.exception;

public class InvalidCardDataException extends BaseException {
    public InvalidCardDataException() {
        super("Invalid card data");
    }

    public InvalidCardDataException(String message) {
        super(message);
    }
}
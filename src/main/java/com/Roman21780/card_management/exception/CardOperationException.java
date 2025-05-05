// CardOperationException.java
package com.Roman21780.card_management.exception;

public class CardOperationException extends BaseException {
    public CardOperationException() {
        super("Invalid card operation");
    }

    public CardOperationException(String message) {
        super(message);
    }
}
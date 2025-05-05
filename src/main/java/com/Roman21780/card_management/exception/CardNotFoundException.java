// CardNotFoundException.java
package com.Roman21780.card_management.exception;

public class CardNotFoundException extends BaseException {
    public CardNotFoundException() {
        super("Card not found");
    }

    public CardNotFoundException(String message) {
        super(message);
    }
}
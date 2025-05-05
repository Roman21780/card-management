// InsufficientFundsException.java
package com.Roman21780.card_management.exception;

public class InsufficientFundsException extends BaseException {
    public InsufficientFundsException() {
        super("Insufficient funds");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}
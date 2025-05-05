// EncryptionException.java
package com.Roman21780.card_management.exception;

public class EncryptionException extends BaseException {
    public EncryptionException() {
        super("Encryption error");
    }

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
package org.haobtc.wallet.exception;

public class ByteFormatException extends IllegalArgumentException {
    public ByteFormatException() {
        super();
    }

    public ByteFormatException(String message) {
        super(message);
    }
}
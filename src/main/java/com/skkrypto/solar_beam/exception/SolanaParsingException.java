package com.skkrypto.solar_beam.exception;

public class SolanaParsingException extends RuntimeException {

    public SolanaParsingException(String message) {
        super(message);
    }

    public SolanaParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}

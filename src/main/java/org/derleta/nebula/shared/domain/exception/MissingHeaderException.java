package org.derleta.nebula.shared.domain.exception;

public class MissingHeaderException extends RuntimeException {
    public MissingHeaderException(String message) {
        super(message);
    }
}

package org.derleta.nebula.nationality.domain.exception;

public class NationalityNotFoundException extends RuntimeException {
    public NationalityNotFoundException(String message) {
        super(message);
    }
}


package org.derleta.nebula.theme.domain.exception;

/** Thrown when a theme with the given ID cannot be found. */
public class ThemeNotFoundException extends RuntimeException {
    public ThemeNotFoundException(String message) {
        super(message);
    }
}


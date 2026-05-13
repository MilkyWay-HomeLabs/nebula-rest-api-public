package org.derleta.nebula.user.domain.exception;

/** Thrown when a requested user does not exist. */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}


package org.derleta.nebula.game.domain.exception;

/** Thrown when a game with the given name or ID already exists. */
public class GameAlreadyExistsException extends RuntimeException {
    public GameAlreadyExistsException(String message) {
        super(message);
    }
}


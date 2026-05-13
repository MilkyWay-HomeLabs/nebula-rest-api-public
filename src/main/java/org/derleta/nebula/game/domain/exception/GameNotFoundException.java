package org.derleta.nebula.game.domain.exception;

/** Thrown when a game with the given ID cannot be found. */
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}


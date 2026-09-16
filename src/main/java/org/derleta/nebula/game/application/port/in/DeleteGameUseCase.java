package org.derleta.nebula.game.application.port.in;

/** Input port — delete a game by ID. */
public interface DeleteGameUseCase {
    void deleteGame(int id);
}


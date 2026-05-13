package org.derleta.nebula.game.application.port.in;

import org.derleta.nebula.game.domain.model.Game;

/** Input port — retrieve a single game by ID. */
public interface GetGameUseCase {
    Game getGame(int id);
}


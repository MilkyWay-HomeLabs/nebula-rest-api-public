package org.derleta.nebula.game.application.port.in;

import org.derleta.nebula.game.domain.model.Game;

/** Input port — create a new game. */
public interface CreateGameUseCase {
    Game createGame(String name, boolean enable, String iconUrl, String pageUrl);
}


package org.derleta.nebula.game.application.port.in;

import org.derleta.nebula.game.domain.model.Game;

/** Input port — update an existing game. */
public interface UpdateGameUseCase {
    Game updateGame(int id, String name, boolean enable, String iconUrl, String pageUrl);
}


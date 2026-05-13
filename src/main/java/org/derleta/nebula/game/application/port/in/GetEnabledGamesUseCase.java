package org.derleta.nebula.game.application.port.in;

import org.derleta.nebula.game.domain.model.Game;

import java.util.List;

/** Input port — retrieve all enabled games. */
public interface GetEnabledGamesUseCase {
    List<Game> getEnabledGames();
}


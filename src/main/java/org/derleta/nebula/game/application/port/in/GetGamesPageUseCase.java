package org.derleta.nebula.game.application.port.in;

import org.springframework.data.domain.Page;
import org.derleta.nebula.game.domain.model.Game;

/** Input port — retrieve a paginated, filtered list of games. */
public interface GetGamesPageUseCase {
    Page<Game> getGamesPage(GamePageQuery query);
}


package org.derleta.nebula.game.application.port.out;

import org.springframework.data.domain.Page;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.application.port.in.GamePageQuery;

import java.util.List;
import java.util.Optional;

/** Output port — data access abstraction for games. */
public interface GameRepositoryPort {

    Optional<Game> findById(int id);

    Page<Game> findAllPaged(GamePageQuery query);

    List<Game> findAllEnabled();

    int getNextId();

    boolean existsById(int id);

    Optional<Game> findByName(String name);

    Optional<Game> findByNameExcludingId(int id, String name);

    Game save(Game game);

    void delete(Game game);
}


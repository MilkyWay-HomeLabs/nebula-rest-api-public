package org.derleta.nebula.game.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.derleta.nebula.game.adapter.out.persistence.mapper.GameMapper;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.application.port.in.*;
import org.derleta.nebula.game.application.port.out.GameRepositoryPort;
import org.derleta.nebula.game.domain.exception.GameAlreadyExistsException;
import org.derleta.nebula.game.domain.exception.GameNotFoundException;

import java.util.List;

/** Application service implementing all game use cases. */
@Service
@RequiredArgsConstructor
public class GameService implements
        GetGameUseCase,
        GetGamesPageUseCase,
        GetEnabledGamesUseCase,
        CreateGameUseCase,
        UpdateGameUseCase,
        DeleteGameUseCase {

    private final GameRepositoryPort gameRepository;

    @Override
    public Game getGame(int id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game with id: " + id + " not found"));
    }

    @Override
    public Page<Game> getGamesPage(GamePageQuery query) {
        return gameRepository.findAllPaged(query);
    }

    @Override
    public List<Game> getEnabledGames() {
        return gameRepository.findAllEnabled();
    }

    @Override
    public Game createGame(String name, boolean enable, String iconUrl, String pageUrl) {
        int id = gameRepository.getNextId();
        if (gameRepository.existsById(id)) {
            throw new GameAlreadyExistsException(
                    "Game with id: " + id + " already exists. Please update the existing record.");
        }
        if (gameRepository.findByName(name).isPresent()) {
            throw new GameAlreadyExistsException(
                    "Game with name: " + name + " already exists. Please choose a different name.");
        }
        Game game = GameMapper.toGame(id, name, enable, iconUrl, pageUrl);
        return gameRepository.save(game);
    }

    @Override
    public Game updateGame(int id, String name, boolean enable, String iconUrl, String pageUrl) {
        gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game with id: " + id + " not found"));
        if (gameRepository.findByNameExcludingId(id, name).isPresent()) {
            throw new GameAlreadyExistsException(
                    "Game with name: " + name + " already exists. Please choose a different name.");
        }
        Game game = GameMapper.toGame(id, name, enable, iconUrl, pageUrl);
        return gameRepository.save(game);
    }

    @Override
    public void deleteGame(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game with id: " + id + " not found"));
        gameRepository.delete(game);
    }
}


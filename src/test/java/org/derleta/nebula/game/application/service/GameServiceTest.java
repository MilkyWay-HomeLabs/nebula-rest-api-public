package org.derleta.nebula.game.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.application.port.in.GamePageQuery;
import org.derleta.nebula.game.application.port.out.GameRepositoryPort;
import org.derleta.nebula.game.domain.exception.GameAlreadyExistsException;
import org.derleta.nebula.game.domain.exception.GameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepositoryPort gameRepository;

    @InjectMocks
    private GameService gameService;

    private final Game game1 = new Game(1, "Game 1", true, "icon1.png", "page1.html");
    private final Game game2 = new Game(2, "Game 2", false, "icon2.png", "page2.html");

    // ── getGame ──────────────────────────────────────────────────────────────

    @Test
    void getGame_existingId_returnsGame() {
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));

        Game result = gameService.getGame(1);

        assertEquals(game1, result);
        verify(gameRepository).findById(1);
    }

    @Test
    void getGame_missingId_throwsGameNotFoundException() {
        when(gameRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.getGame(99));
        verify(gameRepository).findById(99);
    }

    // ── getGamesPage ─────────────────────────────────────────────────────────

    @Test
    void getGamesPage_returnsPage() {
        GamePageQuery query = new GamePageQuery(0, 10, "name", "ASC", null, true);
        Page<Game> page = new PageImpl<>(List.of(game1));
        when(gameRepository.findAllPaged(query)).thenReturn(page);

        Page<Game> result = gameService.getGamesPage(query);

        assertEquals(1, result.getTotalElements());
        verify(gameRepository).findAllPaged(query);
    }

    // ── getEnabledGames ──────────────────────────────────────────────────────

    @Test
    void getEnabledGames_returnsEnabledList() {
        when(gameRepository.findAllEnabled()).thenReturn(List.of(game1));

        List<Game> result = gameService.getEnabledGames();

        assertEquals(1, result.size());
        verify(gameRepository).findAllEnabled();
    }

    // ── createGame ───────────────────────────────────────────────────────────

    @Test
    void createGame_newGame_returnsCreatedGame() {
        when(gameRepository.getNextId()).thenReturn(3);
        when(gameRepository.existsById(3)).thenReturn(false);
        when(gameRepository.findByName("NewGame")).thenReturn(Optional.empty());
        Game created = new Game(3, "NewGame", true, "icon.png", "page.html");
        when(gameRepository.save(any(Game.class))).thenReturn(created);

        Game result = gameService.createGame("NewGame", true, "icon.png", "page.html");

        assertEquals(3, result.id());
        assertEquals("NewGame", result.name());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void createGame_idAlreadyExists_throwsGameAlreadyExistsException() {
        when(gameRepository.getNextId()).thenReturn(1);
        when(gameRepository.existsById(1)).thenReturn(true);

        assertThrows(GameAlreadyExistsException.class,
                () -> gameService.createGame("Game 1", true, "icon.png", "page.html"));
        verify(gameRepository, never()).save(any());
    }

    @Test
    void createGame_nameAlreadyExists_throwsGameAlreadyExistsException() {
        when(gameRepository.getNextId()).thenReturn(3);
        when(gameRepository.existsById(3)).thenReturn(false);
        when(gameRepository.findByName("Game 1")).thenReturn(Optional.of(game1));

        assertThrows(GameAlreadyExistsException.class,
                () -> gameService.createGame("Game 1", true, "icon.png", "page.html"));
        verify(gameRepository, never()).save(any());
    }

    // ── updateGame ───────────────────────────────────────────────────────────

    @Test
    void updateGame_existingGame_returnsUpdatedGame() {
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));
        when(gameRepository.findByNameExcludingId(1, "UpdatedName")).thenReturn(Optional.empty());
        Game updated = new Game(1, "UpdatedName", true, "icon.png", "page.html");
        when(gameRepository.save(any(Game.class))).thenReturn(updated);

        Game result = gameService.updateGame(1, "UpdatedName", true, "icon.png", "page.html");

        assertEquals("UpdatedName", result.name());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void updateGame_missingId_throwsGameNotFoundException() {
        when(gameRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class,
                () -> gameService.updateGame(99, "X", true, "i.png", "p.html"));
        verify(gameRepository, never()).save(any());
    }

    @Test
    void updateGame_nameConflict_throwsGameAlreadyExistsException() {
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));
        when(gameRepository.findByNameExcludingId(1, "Game 2")).thenReturn(Optional.of(game2));

        assertThrows(GameAlreadyExistsException.class,
                () -> gameService.updateGame(1, "Game 2", true, "icon.png", "page.html"));
        verify(gameRepository, never()).save(any());
    }

    // ── deleteGame ───────────────────────────────────────────────────────────

    @Test
    void deleteGame_existingId_callsDelete() {
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));

        gameService.deleteGame(1);

        verify(gameRepository).delete(game1);
    }

    @Test
    void deleteGame_missingId_throwsGameNotFoundException() {
        when(gameRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.deleteGame(99));
        verify(gameRepository, never()).delete(any());
    }
}


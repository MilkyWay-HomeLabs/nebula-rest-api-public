package org.derleta.nebula.game.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.game.adapter.in.rest.dto.GameNewRequest;
import org.derleta.nebula.game.adapter.in.rest.dto.GameResponse;
import org.derleta.nebula.game.application.port.in.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock private GetGameUseCase getGameUseCase;
    @Mock private GetGamesPageUseCase getGamesPageUseCase;
    @Mock private GetEnabledGamesUseCase getEnabledGamesUseCase;
    @Mock private CreateGameUseCase createGameUseCase;
    @Mock private UpdateGameUseCase updateGameUseCase;
    @Mock private DeleteGameUseCase deleteGameUseCase;
    @Mock private CheckAdminRoleUseCase checkAdminRoleUseCase;

    @InjectMocks
    private GameController gameController;

    private Game game1;
    private Game game2;
    private GameNewRequest newRequest;
    private final String token = "valid.token";

    @BeforeEach
    void setUp() {
        game1 = new Game(1, "Game 1", true, "icon1.png", "page1.html");
        game2 = new Game(2, "Game 2", false, "icon2.png", "page2.html");
        newRequest = new GameNewRequest("New Game", true, "new-icon.png", "new-page.html");
    }

    // ── get ──────────────────────────────────────────────────────────────────

    @Test
    void get_validId_returnsOk() {
        when(getGameUseCase.getGame(1)).thenReturn(game1);

        ResponseEntity<GameResponse> response = gameController.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("Game 1", response.getBody().getName());
        verify(getGameUseCase).getGame(1);
    }

    // ── getPage ──────────────────────────────────────────────────────────────

    @Test
    void getPage_returnsPageOfResponses() {
        Page<Game> page = new PageImpl<>(List.of(game1, game2));
        when(getGamesPageUseCase.getGamesPage(any(GamePageQuery.class))).thenReturn(page);

        ResponseEntity<Page<GameResponse>> response =
                gameController.getPage(0, 10, "name", "ASC", null, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
        verify(getGamesPageUseCase).getGamesPage(any(GamePageQuery.class));
    }

    // ── getEnabled ───────────────────────────────────────────────────────────

    @Test
    void getEnabled_returnsEnabledGames() {
        when(getEnabledGamesUseCase.getEnabledGames()).thenReturn(List.of(game1));

        ResponseEntity<List<GameResponse>> response = gameController.getEnabled();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    // ── add ──────────────────────────────────────────────────────────────────

    @Test
    void add_withAdminRole_returnsCreatedGame() {
        when(checkAdminRoleUseCase.notContainsAdminRole(token)).thenReturn(false);
        Game created = new Game(3, "New Game", true, "new-icon.png", "new-page.html");
        when(createGameUseCase.createGame("New Game", true, "new-icon.png", "new-page.html"))
                .thenReturn(created);

        ResponseEntity<GameResponse> response = gameController.add(token, newRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getId());
        verify(createGameUseCase).createGame("New Game", true, "new-icon.png", "new-page.html");
    }

    @Test
    void add_withoutAdminRole_returnsForbidden() {
        when(checkAdminRoleUseCase.notContainsAdminRole(token)).thenReturn(true);

        ResponseEntity<GameResponse> response = gameController.add(token, newRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(createGameUseCase, never()).createGame(any(), anyBoolean(), any(), any());
    }

    @Test
    void add_expiredToken_throwsTokenExpiredException() {
        when(checkAdminRoleUseCase.notContainsAdminRole("expired"))
                .thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> gameController.add("expired", newRequest));
        verify(createGameUseCase, never()).createGame(any(), anyBoolean(), any(), any());
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    void update_withAdminRole_returnsUpdatedGame() {
        when(checkAdminRoleUseCase.notContainsAdminRole(token)).thenReturn(false);
        Game updated = new Game(1, "New Game", true, "new-icon.png", "new-page.html");
        when(updateGameUseCase.updateGame(1, "New Game", true, "new-icon.png", "new-page.html"))
                .thenReturn(updated);

        ResponseEntity<GameResponse> response = gameController.update(token, 1, newRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Game", response.getBody().getName());
        verify(updateGameUseCase).updateGame(1, "New Game", true, "new-icon.png", "new-page.html");
    }

    @Test
    void update_withoutAdminRole_returnsForbidden() {
        when(checkAdminRoleUseCase.notContainsAdminRole(token)).thenReturn(true);

        ResponseEntity<GameResponse> response = gameController.update(token, 1, newRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(updateGameUseCase, never()).updateGame(anyInt(), any(), anyBoolean(), any(), any());
    }

    @Test
    void update_expiredToken_throwsTokenExpiredException() {
        when(checkAdminRoleUseCase.notContainsAdminRole("expired"))
                .thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> gameController.update("expired", 1, newRequest));
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_withAdminRole_returnsOk() {
        when(checkAdminRoleUseCase.notContainsAdminRole(token)).thenReturn(false);

        ResponseEntity<Void> response = gameController.delete(token, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(deleteGameUseCase).deleteGame(1);
    }

    @Test
    void delete_withoutAdminRole_returnsForbidden() {
        when(checkAdminRoleUseCase.notContainsAdminRole(token)).thenReturn(true);

        ResponseEntity<Void> response = gameController.delete(token, 1);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(deleteGameUseCase, never()).deleteGame(anyInt());
    }

    @Test
    void delete_expiredToken_throwsTokenExpiredException() {
        when(checkAdminRoleUseCase.notContainsAdminRole("expired"))
                .thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class, () -> gameController.delete("expired", 1));
        verify(deleteGameUseCase, never()).deleteGame(anyInt());
    }
}


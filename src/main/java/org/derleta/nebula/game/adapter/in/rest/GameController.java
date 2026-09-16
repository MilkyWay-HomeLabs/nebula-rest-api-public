package org.derleta.nebula.game.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.adapter.in.rest.dto.GameNewRequest;
import org.derleta.nebula.game.adapter.in.rest.dto.GameResponse;
import org.derleta.nebula.game.adapter.in.rest.mapper.GameRestMapper;
import org.derleta.nebula.game.application.port.in.*;

import java.util.List;
import java.util.stream.Collectors;

/** Inbound REST adapter for game operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GameController {

    public static final String DEFAULT_PATH = "games";

    private final GetGameUseCase getGameUseCase;
    private final GetGamesPageUseCase getGamesPageUseCase;
    private final GetEnabledGamesUseCase getEnabledGamesUseCase;
    private final CreateGameUseCase createGameUseCase;
    private final UpdateGameUseCase updateGameUseCase;
    private final DeleteGameUseCase deleteGameUseCase;
    private final CheckAdminRoleUseCase checkAdminRoleUseCase;

    @GetMapping("/" + DEFAULT_PATH + "/{id}")
    public ResponseEntity<GameResponse> get(@PathVariable Integer id) {
        return ResponseEntity.ok(GameRestMapper.toResponse(getGameUseCase.getGame(id)));
    }

    @GetMapping("/" + DEFAULT_PATH)
    public ResponseEntity<Page<GameResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "true") boolean enable) {
        GamePageQuery query = new GamePageQuery(page, size, sortBy, sortOrder, name, enable);
        Page<GameResponse> result = getGamesPageUseCase.getGamesPage(query)
                .map(GameRestMapper::toResponse);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/" + DEFAULT_PATH + "/enabled")
    public ResponseEntity<List<GameResponse>> getEnabled() {
        List<GameResponse> result = getEnabledGamesUseCase.getEnabledGames()
                .stream()
                .map(GameRestMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/" + DEFAULT_PATH)
    public ResponseEntity<GameResponse> add(
            @RequestHeader("Authorization") String token,
            @RequestBody GameNewRequest request) {
        if (checkAdminRoleUseCase.notContainsAdminRole(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Game created = createGameUseCase.createGame(
                request.name(), request.enable(), request.iconUrl(), request.pageUrl());
        return ResponseEntity.ok(GameRestMapper.toResponse(created));
    }

    @PutMapping("/" + DEFAULT_PATH + "/{id}")
    public ResponseEntity<GameResponse> update(
            @RequestHeader("Authorization") String token,
            @PathVariable int id,
            @RequestBody GameNewRequest request) {
        if (checkAdminRoleUseCase.notContainsAdminRole(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Game updated = updateGameUseCase.updateGame(
                id, request.name(), request.enable(), request.iconUrl(), request.pageUrl());
        return ResponseEntity.ok(GameRestMapper.toResponse(updated));
    }

    @DeleteMapping("/" + DEFAULT_PATH + "/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        if (checkAdminRoleUseCase.notContainsAdminRole(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        deleteGameUseCase.deleteGame(id);
        return ResponseEntity.ok().build();
    }
}


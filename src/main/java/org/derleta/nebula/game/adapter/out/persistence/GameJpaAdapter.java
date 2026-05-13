package org.derleta.nebula.game.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;
import org.derleta.nebula.game.adapter.out.persistence.mapper.GameMapper;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.adapter.out.persistence.jpa.GameJpaRepository;
import org.derleta.nebula.game.application.port.in.GamePageQuery;
import org.derleta.nebula.game.application.port.out.GameRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Outbound persistence adapter implementing GameRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class GameJpaAdapter implements GameRepositoryPort {

    private final GameJpaRepository repository;

    @Override
    public Optional<Game> findById(int id) {
        return repository.findById(id).map(GameMapper::toGame);
    }

    @Override
    public Page<Game> findAllPaged(GamePageQuery query) {
        Pageable pageable = PageRequest.of(
                query.page(), query.size(),
                Sort.by(Sort.Direction.fromString(query.sortOrder()), query.sortBy()));
        Specification<GameEntity> spec = GameSpecifications.hasAllFilters(query.name(), query.enable());
        Page<GameEntity> entityPage = repository.findAll(spec, pageable);
        List<Game> games = entityPage.stream().map(GameMapper::toGame).collect(Collectors.toList());
        return PageableExecutionUtils.getPage(games, entityPage.getPageable(), entityPage::getTotalElements);
    }

    @Override
    public List<Game> findAllEnabled() {
        return repository.getEnabled().stream()
                .map(GameMapper::toGame)
                .collect(Collectors.toList());
    }

    @Override
    public int getNextId() {
        return repository.getNextId();
    }

    @Override
    public boolean existsById(int id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Game> findByName(String name) {
        return repository.findByName(name).map(GameMapper::toGame);
    }

    @Override
    public Optional<Game> findByNameExcludingId(int id, String name) {
        return repository.findByNameOtherThanSelfId(id, name).map(GameMapper::toGame);
    }

    @Override
    public Game save(Game game) {
        GameEntity entity = GameMapper.toEntity(game);
        GameEntity saved = repository.save(entity);
        return GameMapper.toGame(saved);
    }

    @Override
    public void delete(Game game) {
        GameEntity entity = GameMapper.toEntity(game);
        repository.delete(entity);
    }
}


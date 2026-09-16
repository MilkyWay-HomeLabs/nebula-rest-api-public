package org.derleta.nebula.game.adapter.out.persistence.jpa;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;
import org.derleta.nebula.game.adapter.out.persistence.GameSpecifications;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class GameJpaRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GameJpaRepository gameJpaRepository;

    @Test
    @Transactional
    void getNextId_shouldReturnNextAvailableId() {
        GameEntity game = new GameEntity();
        game.setName("Test Game for NextId");
        game.setEnable(true);
        game.setIconUrl("https://example.com/icon.png");
        game.setPageUrl("https://example.com/game");

        GameEntity savedGame = gameJpaRepository.save(game);
        int nextId = gameJpaRepository.getNextId();

        assertEquals(savedGame.getId() + 1, nextId);
    }

    @Test
    @Transactional
    void findAll_withSpecification_shouldReturnFilteredResults() {
        GameEntity game1 = new GameEntity();
        game1.setName("Test Game 1");
        game1.setEnable(true);
        game1.setIconUrl("https://example.com/icon1.png");
        game1.setPageUrl("https://example.com/game1");

        GameEntity game2 = new GameEntity();
        game2.setName("Test Game 2");
        game2.setEnable(false);
        game2.setIconUrl("https://example.com/icon2.png");
        game2.setPageUrl("https://example.com/game2");

        gameJpaRepository.save(game1);
        gameJpaRepository.save(game2);
        gameJpaRepository.flush();
        entityManager.clear();

        Specification<GameEntity> spec = GameSpecifications.hasAllFilters("Test Game", true);
        Pageable pageable = PageRequest.of(0, 10);

        Page<GameEntity> result = gameJpaRepository.findAll(spec, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().stream().anyMatch(g -> g.getName().equals("Test Game 1")));
        assertFalse(result.getContent().stream().anyMatch(g -> g.getName().equals("Test Game 2")));
    }

    @Test
    @Transactional
    void findByName_existingName_shouldReturnGame() {
        String gameName = "Unique Test Game Name";
        GameEntity game = new GameEntity();
        game.setName(gameName);
        game.setEnable(true);
        game.setIconUrl("https://example.com/icon.png");
        game.setPageUrl("https://example.com/game");

        gameJpaRepository.save(game);
        gameJpaRepository.flush();
        entityManager.clear();

        Optional<GameEntity> result = gameJpaRepository.findByName(gameName);

        assertTrue(result.isPresent());
        assertEquals(gameName, result.get().getName());
    }

    @Test
    @Transactional
    void findByName_nonExistingName_shouldReturnEmptyOptional() {
        Optional<GameEntity> result = gameJpaRepository.findByName("Non Existing Game Name");
        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void findByNameOtherThanSelfId_existingNameDifferentId_shouldReturnGame() {
        String gameName = "Duplicate Test Game Name";
        GameEntity game1 = new GameEntity();
        game1.setName(gameName);
        game1.setEnable(true);
        game1.setIconUrl("https://example.com/icon1.png");
        game1.setPageUrl("https://example.com/game1");

        GameEntity savedGame1 = gameJpaRepository.save(game1);
        gameJpaRepository.flush();
        entityManager.clear();

        Optional<GameEntity> result = gameJpaRepository.findByNameOtherThanSelfId(savedGame1.getId() + 1, gameName);

        assertTrue(result.isPresent());
        assertEquals(gameName, result.get().getName());
        assertEquals(savedGame1.getId(), result.get().getId());
    }

    @Test
    @Transactional
    void findByNameOtherThanSelfId_existingNameSameId_shouldReturnEmptyOptional() {
        String gameName = "Self Test Game Name";
        GameEntity game = new GameEntity();
        game.setName(gameName);
        game.setEnable(true);
        game.setIconUrl("https://example.com/icon.png");
        game.setPageUrl("https://example.com/game");

        GameEntity savedGame = gameJpaRepository.save(game);
        gameJpaRepository.flush();
        entityManager.clear();

        Optional<GameEntity> result = gameJpaRepository.findByNameOtherThanSelfId(savedGame.getId(), gameName);

        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void getEnabled_shouldReturnOnlyEnabledGames() {
        GameEntity enabledGame = new GameEntity();
        enabledGame.setName("Enabled Test Game");
        enabledGame.setEnable(true);
        enabledGame.setIconUrl("https://example.com/icon1.png");
        enabledGame.setPageUrl("https://example.com/game1");

        GameEntity disabledGame = new GameEntity();
        disabledGame.setName("Disabled Test Game");
        disabledGame.setEnable(false);
        disabledGame.setIconUrl("https://example.com/icon2.png");
        disabledGame.setPageUrl("https://example.com/game2");

        gameJpaRepository.save(enabledGame);
        gameJpaRepository.save(disabledGame);
        gameJpaRepository.flush();
        entityManager.clear();

        List<GameEntity> enabledGames = gameJpaRepository.getEnabled();

        assertNotNull(enabledGames);
        assertTrue(enabledGames.stream().anyMatch(g -> g.getName().equals("Enabled Test Game")));
        assertFalse(enabledGames.stream().anyMatch(g -> g.getName().equals("Disabled Test Game")));
        assertTrue(enabledGames.stream().allMatch(GameEntity::getEnable));
    }
}


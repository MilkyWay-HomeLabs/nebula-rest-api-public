package org.derleta.nebula.domain.mapper;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.adapter.out.persistence.mapper.GameMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    @Test
    void toGames_validEntities_returnsGames() {
        // Arrange
        GameEntity entity1 = new GameEntity();
        entity1.setId(1);
        entity1.setName("Game 1");
        entity1.setEnable(true);
        entity1.setIconUrl("https://example.com/icon1.png");
        entity1.setPageUrl("https://example.com/game1");

        GameEntity entity2 = new GameEntity();
        entity2.setId(2);
        entity2.setName("Game 2");
        entity2.setEnable(false);
        entity2.setIconUrl("https://example.com/icon2.png");
        entity2.setPageUrl("https://example.com/game2");

        List<GameEntity> entities = Arrays.asList(entity1, entity2);

        // Act
        List<Game> result = GameMapper.toGames(entities);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        Game game1 = result.getFirst();
        assertEquals(1, game1.id());
        assertEquals("Game 1", game1.name());
        assertTrue(game1.enable());
        assertEquals("https://example.com/icon1.png", game1.iconUrl());
        assertEquals("https://example.com/game1", game1.pageUrl());
        
        Game game2 = result.get(1);
        assertEquals(2, game2.id());
        assertEquals("Game 2", game2.name());
        assertFalse(game2.enable());
        assertEquals("https://example.com/icon2.png", game2.iconUrl());
        assertEquals("https://example.com/game2", game2.pageUrl());
    }

    @Test
    void toGames_emptyList_returnsEmptyList() {
        // Arrange
        List<GameEntity> entities = Collections.emptyList();

        // Act
        List<Game> result = GameMapper.toGames(entities);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toGame_validEntity_returnsGame() {
        // Arrange
        GameEntity entity = new GameEntity();
        entity.setId(1);
        entity.setName("Test Game");
        entity.setEnable(true);
        entity.setIconUrl("https://example.com/icon.png");
        entity.setPageUrl("https://example.com/game");

        // Act
        Game result = GameMapper.toGame(entity);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Test Game", result.name());
        assertTrue(result.enable());
        assertEquals("https://example.com/icon.png", result.iconUrl());
        assertEquals("https://example.com/game", result.pageUrl());
    }

    @Test
    void toGame_nullEntity_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> GameMapper.toGame(null)
        );
    }

    @Test
    void toGame_entityWithNullFields_returnsGameWithNullFields() {
        // Arrange
        GameEntity entity = new GameEntity();
        entity.setId(1);

        // Act
        Game result = GameMapper.toGame(entity);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertNull(result.name());
        assertFalse(result.enable());
        assertNull(result.iconUrl());
        assertNull(result.pageUrl());
    }

    @Test
    void toGame_validAttributes_returnsGame() {
        // Arrange
        Integer id = 1;
        String name = "Test Game";
        Boolean enable = true;
        String iconUrl = "https://example.com/icon.png";
        String pageUrl = "https://example.com/game";

        // Act
        Game result = GameMapper.toGame(id, name, enable, iconUrl, pageUrl);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Test Game", result.name());
        assertTrue(result.enable());
        assertEquals("https://example.com/icon.png", result.iconUrl());
        assertEquals("https://example.com/game", result.pageUrl());
    }

    @Test
    void toGame_nullAttributes_returnsGameWithNullFields() {
        // Arrange
        Integer id = 1;

        // Act
        Game result = GameMapper.toGame(id, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertNull(result.name());
        assertFalse(result.enable());
        assertNull(result.iconUrl());
        assertNull(result.pageUrl());
    }

    @Test
    void toEntity_validGame_returnsGameEntity() {
        // Arrange
        Game game = new Game(1, "Test Game", true, "https://example.com/icon.png", "https://example.com/game");

        // Act
        GameEntity result = GameMapper.toEntity(game);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Game", result.getName());
        assertTrue(result.getEnable());
        assertEquals("https://example.com/icon.png", result.getIconUrl());
        assertEquals("https://example.com/game", result.getPageUrl());
    }

    @Test
    void toEntity_nullGame_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> GameMapper.toEntity(null)
        );
    }

}

package org.derleta.nebula.game.adapter.in.rest.mapper;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.game.adapter.in.rest.dto.GameResponse;

import static org.junit.jupiter.api.Assertions.*;

class GameRestMapperTest {

    @Test
    void toResponse_validGame_returnsGameResponse() {
        Game game = new Game(1, "Test Game", true, "icon.png", "page.html");

        GameResponse result = GameRestMapper.toResponse(game);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Game", result.getName());
        assertTrue(result.getEnable());
        assertEquals("icon.png", result.getIconUrl());
        assertEquals("page.html", result.getPageUrl());
    }

    @Test
    void toResponse_disabledGame_enableIsFalse() {
        Game game = new Game(2, "Disabled Game", false, "icon2.png", "page2.html");

        GameResponse result = GameRestMapper.toResponse(game);

        assertFalse(result.getEnable());
    }
}


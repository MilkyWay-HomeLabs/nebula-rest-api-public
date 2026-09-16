package org.derleta.nebula.domain.mapper;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.adapter.out.persistence.mapper.ThemeMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThemeMapperTest {

    @Test
    void toThemes_validEntities_returnsThemes() {
        // Arrange
        ThemeEntity entity1 = new ThemeEntity();
        entity1.setId(1);
        entity1.setName("Dark");

        ThemeEntity entity2 = new ThemeEntity();
        entity2.setId(2);
        entity2.setName("Light");

        List<ThemeEntity> entities = Arrays.asList(entity1, entity2);

        // Act
        List<Theme> result = ThemeMapper.toThemes(entities);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        Theme theme1 = result.getFirst();
        assertEquals(1, theme1.id());
        assertEquals("Dark", theme1.name());
        
        Theme theme2 = result.get(1);
        assertEquals(2, theme2.id());
        assertEquals("Light", theme2.name());
    }

    @Test
    void toThemes_emptyList_returnsEmptyList() {
        // Arrange
        List<ThemeEntity> entities = Collections.emptyList();

        // Act
        List<Theme> result = ThemeMapper.toThemes(entities);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toTheme_validEntity_returnsTheme() {
        // Arrange
        ThemeEntity entity = new ThemeEntity();
        entity.setId(1);
        entity.setName("Dark");

        // Act
        Theme result = ThemeMapper.toTheme(entity);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Dark", result.name());
    }

    @Test
    void toTheme_nullEntity_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> ThemeMapper.toTheme(null)
        );
    }

    @Test
    void toTheme_entityWithNullName_returnsThemeWithNullName() {
        // Arrange
        ThemeEntity entity = new ThemeEntity();
        entity.setId(1);
        // Name is null

        // Act
        Theme result = ThemeMapper.toTheme(entity);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertNull(result.name());
    }

    @Test
    void toEntity_validTheme_returnsThemeEntity() {
        // Arrange
        Theme theme = new Theme(1, "Dark");

        // Act
        ThemeEntity result = ThemeMapper.toEntity(theme);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Dark", result.getName());
    }

    @Test
    void toEntity_nullTheme_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> ThemeMapper.toEntity(null)
        );
    }

    @Test
    void toEntity_themeWithNullName_returnsThemeEntityWithNullName() {
        // Arrange
        Theme theme = new Theme(1, null);

        // Act
        ThemeEntity result = ThemeMapper.toEntity(theme);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertNull(result.getName());
    }

}

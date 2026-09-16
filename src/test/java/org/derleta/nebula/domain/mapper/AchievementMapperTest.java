package org.derleta.nebula.domain.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementEntity;
import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.adapter.out.persistence.mapper.AchievementMapper;
import org.derleta.nebula.userachievement.adapter.out.persistence.mapper.AchievementLevelMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AchievementMapperTest {

    @Test
    void toAchievement_validEntity_returnsAchievement() {
        // Arrange
        AchievementEntity entity = new AchievementEntity();
        entity.setId(1);
        entity.setName("Test Achievement");
        entity.setMinValue(0);
        entity.setMaxValue(100);
        entity.setDescription("Test Description");
        entity.setIconUrl("http://example.com/icon.png");

        List<AchievementLevel> achievementLevels = List.of(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );

        try (MockedStatic<AchievementLevelMapper> mockedStatic = Mockito.mockStatic(AchievementLevelMapper.class)) {
            mockedStatic.when(() -> AchievementLevelMapper.toAchievementLevels(any()))
                    .thenReturn(achievementLevels);

            // Act
            Achievement result = AchievementMapper.toAchievement(entity);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.id());
            assertEquals("Test Achievement", result.name());
            assertEquals(0, result.minValue());
            assertEquals(100, result.maxValue());
            assertEquals("Test Description", result.description());
            assertEquals("http://example.com/icon.png", result.iconUrl());
            assertEquals(achievementLevels, result.levels());
            mockedStatic.verify(() -> AchievementLevelMapper.toAchievementLevels(entity.getLevels()));
        }
    }

    @Test
    void toAchievement_nullEntity_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> AchievementMapper.toAchievement(null)
        );
    }

    @Test
    void toAchievement_entityWithNullFields_returnsAchievementWithNullFields() {
        // Arrange
        AchievementEntity entity = new AchievementEntity();
        entity.setId(1);
        List<AchievementLevel> emptyLevels = Collections.emptyList();

        try (MockedStatic<AchievementLevelMapper> mockedStatic = Mockito.mockStatic(AchievementLevelMapper.class)) {
            mockedStatic.when(() -> AchievementLevelMapper.toAchievementLevels(any()))
                    .thenReturn(emptyLevels);

            // Act
            Achievement result = AchievementMapper.toAchievement(entity);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.id());
            assertNull(result.name());
            assertEquals(0, result.minValue());
            assertEquals(0, result.maxValue());
            assertNull(result.description());
            assertNull(result.iconUrl());
            assertEquals(emptyLevels, result.levels());
            mockedStatic.verify(() -> AchievementLevelMapper.toAchievementLevels(entity.getLevels()));
        }
    }

    @Test
    void toAchievement_entityWithEmptyLevels_returnsAchievementWithEmptyLevels() {
        // Arrange
        AchievementEntity entity = new AchievementEntity();
        entity.setId(1);
        entity.setName("Test Achievement");
        entity.setMinValue(0);
        entity.setMaxValue(100);
        entity.setDescription("Test Description");
        entity.setIconUrl("http://example.com/icon.png");
        entity.setLevels(Collections.emptyList());

        List<AchievementLevel> emptyLevels = Collections.emptyList();

        try (MockedStatic<AchievementLevelMapper> mockedStatic = Mockito.mockStatic(AchievementLevelMapper.class)) {
            mockedStatic.when(() -> AchievementLevelMapper.toAchievementLevels(any()))
                    .thenReturn(emptyLevels);

            // Act
            Achievement result = AchievementMapper.toAchievement(entity);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.id());
            assertEquals("Test Achievement", result.name());
            assertEquals(0, result.minValue());
            assertEquals(100, result.maxValue());
            assertEquals("Test Description", result.description());
            assertEquals("http://example.com/icon.png", result.iconUrl());
            assertEquals(emptyLevels, result.levels());
            mockedStatic.verify(() -> AchievementLevelMapper.toAchievementLevels(entity.getLevels()));
        }
    }

}

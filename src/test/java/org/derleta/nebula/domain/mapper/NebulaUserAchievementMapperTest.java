package org.derleta.nebula.domain.mapper;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.domain.model.NebulaUserAchievement;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.domain.mapper.NebulaUserAchievementMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NebulaUserAchievementMapperTest {

    @NotNull
    private static List<UserAchievement> getUserAchievements(List<AchievementLevel> levels) {
        Achievement achievement1 = new Achievement(1, "Achievement 1", 0, 100, "Description 1", "https://example.com/icon1.png", levels);
        Achievement achievement2 = new Achievement(2, "Achievement 2", 0, 200, "Description 2", "https://example.com/icon2.png", levels);

        UserAchievement userAchievement1 = new UserAchievement(1000L, 1, 30, 1, "30%", achievement1);
        UserAchievement userAchievement2 = new UserAchievement(1000L, 2, 60, 2, "30%", achievement2);

        return Arrays.asList(userAchievement1, userAchievement2);
    }

    @Test
    void toList_validItems_returnsNebulaUserAchievements() {
        // Arrange
        List<AchievementLevel> levels = Arrays.asList(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );

        List<UserAchievement> userAchievements = getUserAchievements(levels);

        // Act
        List<NebulaUserAchievement> result = NebulaUserAchievementMapper.toList(userAchievements);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        NebulaUserAchievement nebulaUserAchievement1 = result.getFirst();
        assertEquals(1, nebulaUserAchievement1.id());
        assertEquals("Achievement 1", nebulaUserAchievement1.name());
        assertEquals(0, nebulaUserAchievement1.minValue());
        assertEquals(100, nebulaUserAchievement1.maxValue());
        assertEquals(30, nebulaUserAchievement1.value());
        assertEquals(1, nebulaUserAchievement1.level());
        assertEquals("30%", nebulaUserAchievement1.progress());
        assertEquals("Description 1", nebulaUserAchievement1.description());
        assertEquals("https://example.com/icon1.png", nebulaUserAchievement1.iconUrl());
        assertEquals(levels, nebulaUserAchievement1.levels());
        
        NebulaUserAchievement nebulaUserAchievement2 = result.get(1);
        assertEquals(2, nebulaUserAchievement2.id());
        assertEquals("Achievement 2", nebulaUserAchievement2.name());
        assertEquals(0, nebulaUserAchievement2.minValue());
        assertEquals(200, nebulaUserAchievement2.maxValue());
        assertEquals(60, nebulaUserAchievement2.value());
        assertEquals(2, nebulaUserAchievement2.level());
        assertEquals("30%", nebulaUserAchievement2.progress());
        assertEquals("Description 2", nebulaUserAchievement2.description());
        assertEquals("https://example.com/icon2.png", nebulaUserAchievement2.iconUrl());
        assertEquals(levels, nebulaUserAchievement2.levels());
    }

    @Test
    void toList_emptyList_returnsEmptyList() {
        // Arrange
        List<UserAchievement> userAchievements = Collections.emptyList();

        // Act
        List<NebulaUserAchievement> result = NebulaUserAchievementMapper.toList(userAchievements);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toItem_validItem_returnsNebulaUserAchievement() {
        // Arrange
        List<AchievementLevel> levels = Arrays.asList(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );

        Achievement achievement = new Achievement(1, "Achievement 1", 0, 100, "Description 1", "https://example.com/icon1.png", levels);
        UserAchievement userAchievement = new UserAchievement(1000L, 1, 30, 1, "30%", achievement);

        // Act
        NebulaUserAchievement result = NebulaUserAchievementMapper.toItem(userAchievement);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Achievement 1", result.name());
        assertEquals(0, result.minValue());
        assertEquals(100, result.maxValue());
        assertEquals(30, result.value());
        assertEquals(1, result.level());
        assertEquals("30%", result.progress());
        assertEquals("Description 1", result.description());
        assertEquals("https://example.com/icon1.png", result.iconUrl());
        assertEquals(levels, result.levels());
    }

    @Test
    void toItem_nullItem_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> NebulaUserAchievementMapper.toItem(null)
        );
    }

    @Test
    void toItem_itemWithNullAchievement_throwsNullPointerException() {
        // Arrange
        UserAchievement userAchievement = new UserAchievement(1000L, 1, 30, 1, "30%", null);

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> NebulaUserAchievementMapper.toItem(userAchievement)
        );
    }

    @Test
    void toItem_itemWithNullFields_returnsNebulaUserAchievementWithNullFields() {
        // Arrange
        Achievement achievement = new Achievement(1, null, 0, 0, null, null, null);
        UserAchievement userAchievement = new UserAchievement(1000L, 1, 30, 1, null, achievement);

        // Act
        NebulaUserAchievement result = NebulaUserAchievementMapper.toItem(userAchievement);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
        assertNull(result.name());
        assertEquals(0, result.minValue());
        assertEquals(0, result.maxValue());
        assertEquals(30, result.value());
        assertEquals(1, result.level());
        assertNull(result.progress());
        assertNull(result.description());
        assertNull(result.iconUrl());
        assertNull(result.levels());
    }

}

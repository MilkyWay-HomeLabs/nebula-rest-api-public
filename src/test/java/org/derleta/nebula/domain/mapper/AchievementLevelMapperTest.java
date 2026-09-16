package org.derleta.nebula.domain.mapper;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementLevelEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.AchievementLevelId;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.adapter.out.persistence.mapper.AchievementLevelMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AchievementLevelMapperTest {

    @Test
    void toAchievementLevels_validEntities_returnsPopulatedList() {
        // Arrange
        AchievementLevelId id1 = new AchievementLevelId();
        id1.setAchievementId(1);
        id1.setLevel(1);
        AchievementLevelEntity entity1 = new AchievementLevelEntity();
        entity1.setId(id1);
        entity1.setValue(50);

        AchievementLevelId id2 = new AchievementLevelId();
        id2.setAchievementId(1);
        id2.setLevel(2);
        AchievementLevelEntity entity2 = new AchievementLevelEntity();
        entity2.setId(id2);
        entity2.setValue(75);

        List<AchievementLevelEntity> entities = List.of(entity1, entity2);

        // Act
        List<AchievementLevel> result = AchievementLevelMapper.toAchievementLevels(entities);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).level());
        assertEquals(50, result.get(0).value());
        assertEquals(2, result.get(1).level());
        assertEquals(75, result.get(1).value());
    }

    @Test
    void toAchievementLevels_nullInput_returnsEmptyList() {
        // Act
        List<AchievementLevel> result = AchievementLevelMapper.toAchievementLevels(null);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void toAchievementLevels_emptyList_returnsEmptyList() {
        // Arrange
        List<AchievementLevelEntity> entities = Collections.emptyList();

        // Act
        List<AchievementLevel> result = AchievementLevelMapper.toAchievementLevels(entities);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void toAchievementLevel_validEntity_returnsAchievementLevel() {
        // Arrange
        AchievementLevelId id = new AchievementLevelId();
        id.setAchievementId(1);
        id.setLevel(5);
        AchievementLevelEntity entity = new AchievementLevelEntity();
        entity.setId(id);
        entity.setValue(100);

        // Act
        AchievementLevel result = AchievementLevelMapper.toAchievementLevel(entity);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.level());
        assertEquals(100, result.value());
    }

    @Test
    void toAchievementLevel_nullEntity_throwsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AchievementLevelMapper.toAchievementLevel(null)
        );
        assertEquals("AchievementLevelEntity cannot be null", exception.getMessage());
    }

    @Test
    void toAchievementLevel_nullEntityId_throwsNullPointerException() {
        // Arrange
        AchievementLevelEntity entity = new AchievementLevelEntity();
        entity.setValue(100);

        // Act & Assert
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AchievementLevelMapper.toAchievementLevel(entity)
        );
        assertEquals("AchievementLevelEntity must have a valid ID", exception.getMessage());
    }

}

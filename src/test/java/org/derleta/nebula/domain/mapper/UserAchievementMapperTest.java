package org.derleta.nebula.domain.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.UserAchievementId;
import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.adapter.out.persistence.mapper.AchievementMapper;
import org.derleta.nebula.userachievement.adapter.out.persistence.mapper.UserAchievementMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UserAchievementMapperTest {

    @Test
    void toUserAchievements_validEntities_returnsUserAchievements() {
        // Arrange
        UserAchievementId id1 = new UserAchievementId();
        id1.setUserId(1000L);
        id1.setAchievementId(1);
        
        UserAchievementEntity entity1 = new UserAchievementEntity();
        entity1.setId(id1);
        entity1.setProgress(3000);
        entity1.setLevel(1);
        entity1.setValue(30);
        
        AchievementEntity achievementEntity1 = new AchievementEntity();
        achievementEntity1.setId(1);
        entity1.setAchievement(achievementEntity1);
        
        UserAchievementId id2 = new UserAchievementId();
        id2.setUserId(1000L);
        id2.setAchievementId(2);
        
        UserAchievementEntity entity2 = new UserAchievementEntity();
        entity2.setId(id2);
        entity2.setProgress(5000);
        entity2.setLevel(2);
        entity2.setValue(50);
        
        AchievementEntity achievementEntity2 = new AchievementEntity();
        achievementEntity2.setId(2);
        entity2.setAchievement(achievementEntity2);
        
        List<UserAchievementEntity> entities = Arrays.asList(entity1, entity2);
        
        List<AchievementLevel> levels = Arrays.asList(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );
        
        Achievement achievement1 = new Achievement(1, "Achievement 1", 0, 100, "Description 1", "http://example.com/icon1.png", levels);
        Achievement achievement2 = new Achievement(2, "Achievement 2", 0, 200, "Description 2", "http://example.com/icon2.png", levels);
        
        try (MockedStatic<AchievementMapper> mockedStatic = Mockito.mockStatic(AchievementMapper.class)) {
            // Mock the static method call to AchievementMapper.toAchievement
            mockedStatic.when(() -> AchievementMapper.toAchievement(achievementEntity1))
                    .thenReturn(achievement1);
            mockedStatic.when(() -> AchievementMapper.toAchievement(achievementEntity2))
                    .thenReturn(achievement2);
            
            // Act
            List<UserAchievement> result = UserAchievementMapper.toUserAchievements(entities);
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            
            UserAchievement userAchievement1 = result.getFirst();
            assertEquals(1000L, userAchievement1.userId());
            assertEquals(1, userAchievement1.achievementId());
            assertEquals(30, userAchievement1.value());
            assertEquals(1, userAchievement1.level());
            assertEquals("30,00%", userAchievement1.progress());
            assertEquals(achievement1, userAchievement1.achievement());
            
            UserAchievement userAchievement2 = result.get(1);
            assertEquals(1000L, userAchievement2.userId());
            assertEquals(2, userAchievement2.achievementId());
            assertEquals(50, userAchievement2.value());
            assertEquals(2, userAchievement2.level());
            assertEquals("50,00%", userAchievement2.progress());
            assertEquals(achievement2, userAchievement2.achievement());
            
            // Verify that AchievementMapper.toAchievement was called with the correct arguments
            mockedStatic.verify(() -> AchievementMapper.toAchievement(achievementEntity1));
            mockedStatic.verify(() -> AchievementMapper.toAchievement(achievementEntity2));
        }
    }
    
    @Test
    void toUserAchievements_emptyList_returnsEmptyList() {
        // Arrange
        List<UserAchievementEntity> entities = Collections.emptyList();
        
        // Act
        List<UserAchievement> result = UserAchievementMapper.toUserAchievements(entities);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void toUserAchievement_validEntity_returnsUserAchievement() {
        // Arrange
        UserAchievementId id = new UserAchievementId();
        id.setUserId(1000L);
        id.setAchievementId(1);
        
        UserAchievementEntity entity = new UserAchievementEntity();
        entity.setId(id);
        entity.setProgress(3000);
        entity.setLevel(1);
        entity.setValue(30);
        
        AchievementEntity achievementEntity = new AchievementEntity();
        achievementEntity.setId(1);
        entity.setAchievement(achievementEntity);
        
        List<AchievementLevel> levels = Arrays.asList(
                new AchievementLevel(1, 25),
                new AchievementLevel(2, 50)
        );
        
        Achievement achievement = new Achievement(1, "Achievement 1", 0, 100, "Description 1", "http://example.com/icon1.png", levels);
        
        try (MockedStatic<AchievementMapper> mockedStatic = Mockito.mockStatic(AchievementMapper.class)) {
            // Mock the static method call to AchievementMapper.toAchievement
            mockedStatic.when(() -> AchievementMapper.toAchievement(any()))
                    .thenReturn(achievement);
            
            // Act
            UserAchievement result = UserAchievementMapper.toUserAchievement(entity);
            
            // Assert
            assertNotNull(result);
            assertEquals(1000L, result.userId());
            assertEquals(1, result.achievementId());
            assertEquals(30, result.value());
            assertEquals(1, result.level());
            assertEquals("30,00%", result.progress());
            assertEquals(achievement, result.achievement());
            
            // Verify that AchievementMapper.toAchievement was called with the correct argument
            mockedStatic.verify(() -> AchievementMapper.toAchievement(achievementEntity));
        }
    }
    
    @Test
    void toUserAchievement_nullEntity_throwsNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> UserAchievementMapper.toUserAchievement(null)
        );
    }
    
    @Test
    void toUserAchievement_entityWithNullId_throwsNullPointerException() {
        // Arrange
        UserAchievementEntity entity = new UserAchievementEntity();

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> UserAchievementMapper.toUserAchievement(entity)
        );
    }
    
    @Test
    void toUserAchievement_entityWithNullAchievement_returnsUserAchievementWithNullAchievement() {
        // Arrange
        UserAchievementId id = new UserAchievementId();
        id.setUserId(1000L);
        id.setAchievementId(1);
        
        UserAchievementEntity entity = new UserAchievementEntity();
        entity.setId(id);
        entity.setProgress(3000);
        entity.setLevel(1);
        entity.setValue(30);

        try (MockedStatic<AchievementMapper> mockedStatic = Mockito.mockStatic(AchievementMapper.class)) {
            // Mock the static method call to AchievementMapper.toAchievement
            mockedStatic.when(() -> AchievementMapper.toAchievement(any()))
                    .thenReturn(null);
            
            // Act
            UserAchievement result = UserAchievementMapper.toUserAchievement(entity);
            
            // Assert
            assertNotNull(result);
            assertEquals(1000L, result.userId());
            assertEquals(1, result.achievementId());
            assertEquals(30, result.value());
            assertEquals(1, result.level());
            assertEquals("30,00%", result.progress());
            assertNull(result.achievement());
            
            // Verify that AchievementMapper.toAchievement was called with the correct argument
            mockedStatic.verify(() -> AchievementMapper.toAchievement(null));
        }
    }

}

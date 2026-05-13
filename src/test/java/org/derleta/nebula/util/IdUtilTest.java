package org.derleta.nebula.util;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.account.adapter.out.persistence.IdUtil;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.UserAchievementId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IdUtilTest {

    /**
     * Tests for the {@link IdUtil#getUserAchievementId(long, int)} method.
     * This method generates a {@link UserAchievementId} object using the provided
     * userId and achievementId values.
     * <p>
     * Scenarios covered:
     * 1. Verifying the creation of UserAchievementId with valid inputs.
     * 2. Confirming that returned object's fields match input values.
     */

    @Test
    void getUserAchievementId_validInputs_shouldReturnUserAchievementIdWithCorrectFields() {
        // Arrange
        long userId = 123L;
        int achievementId = 456;

        // Act
        UserAchievementId userAchievementId = IdUtil.getUserAchievementId(userId, achievementId);

        // Assert
        assertNotNull(userAchievementId);
        assertEquals(userId, userAchievementId.getUserId());
        assertEquals(achievementId, userAchievementId.getAchievementId());
    }

    @Test
    void getUserAchievementId_edgeCaseValues_shouldReturnUserAchievementId() {
        // Arrange
        long userId = 0L;
        int achievementId = 0;

        // Act
        UserAchievementId userAchievementId = IdUtil.getUserAchievementId(userId, achievementId);

        // Assert
        assertNotNull(userAchievementId);
        assertEquals(userId, userAchievementId.getUserId());
        assertEquals(achievementId, userAchievementId.getAchievementId());
    }

    @Test
    void getUserAchievementId_multipleCalls_shouldReturnDistinctNewInstances() {
        // Arrange
        long userId1 = 123L;
        int achievementId1 = 456;

        long userId2 = 789L;
        int achievementId2 = 101;

        // Act
        UserAchievementId userAchievementId1 = IdUtil.getUserAchievementId(userId1, achievementId1);
        UserAchievementId userAchievementId2 = IdUtil.getUserAchievementId(userId2, achievementId2);

        // Assert
        assertNotNull(userAchievementId1);
        assertNotNull(userAchievementId2);
        assertEquals(userId1, userAchievementId1.getUserId());
        assertEquals(achievementId1, userAchievementId1.getAchievementId());
        assertEquals(userId2, userAchievementId2.getUserId());
        assertEquals(achievementId2, userAchievementId2.getAchievementId());
    }

}

package org.derleta.nebula.domain.builder.impl;

import org.junit.jupiter.api.Test;
import org.derleta.nebula.userachievement.domain.builder.UserAchievementBuilder;
import org.derleta.nebula.userachievement.domain.builder.impl.UserAchievementBuilderImpl;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;

import static org.junit.jupiter.api.Assertions.*;

class UserAchievementBuilderImplTest {

    @Test
    void testGetFormattedProgress() {
        UserAchievementBuilder builder = new UserAchievementBuilderImpl();
        builder.progress(1234);
        UserAchievement userAchievements = builder.build();
        assertEquals("12,34%", userAchievements.progress());
        builder.progress(0);
        userAchievements = builder.build();
        assertEquals("0,00%", userAchievements.progress());
        builder.progress(10000);
        userAchievements = builder.build();
        assertEquals("100,00%", userAchievements.progress());
        builder.progress(5678);
        userAchievements = builder.build();
        assertEquals("56,78%", userAchievements.progress());
        builder.progress(1);
        userAchievements = builder.build();
        assertEquals("0,01%", userAchievements.progress());
    }

}
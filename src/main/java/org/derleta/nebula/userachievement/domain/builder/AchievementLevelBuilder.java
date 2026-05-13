package org.derleta.nebula.userachievement.domain.builder;

import org.derleta.nebula.userachievement.domain.model.AchievementLevel;

public interface AchievementLevelBuilder {

    AchievementLevel build();

    AchievementLevelBuilder level(int level);

    AchievementLevelBuilder value(int value);

}

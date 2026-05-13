package org.derleta.nebula.userachievement.domain.builder;

import org.derleta.nebula.userachievement.domain.model.AchievementLevel;
import org.derleta.nebula.userachievement.domain.model.NebulaUserAchievement;

import java.util.List;

public interface NebulaUserAchievementBuilder {

    NebulaUserAchievement build();

    NebulaUserAchievementBuilder id(int id);

    NebulaUserAchievementBuilder minValue(int minValue);

    NebulaUserAchievementBuilder maxValue(int maxValue);

    NebulaUserAchievementBuilder value(int value);

    NebulaUserAchievementBuilder level(int level);

    NebulaUserAchievementBuilder name(String name);

    NebulaUserAchievementBuilder progress(String progress);

    NebulaUserAchievementBuilder description(String description);

    NebulaUserAchievementBuilder iconUrl(String iconUrl);

    NebulaUserAchievementBuilder levels(List<AchievementLevel> levels);

}

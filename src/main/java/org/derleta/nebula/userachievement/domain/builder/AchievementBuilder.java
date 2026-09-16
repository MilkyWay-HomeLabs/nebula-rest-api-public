package org.derleta.nebula.userachievement.domain.builder;

import org.derleta.nebula.userachievement.domain.model.Achievement;
import org.derleta.nebula.userachievement.domain.model.AchievementLevel;

import java.util.List;

public interface AchievementBuilder {

    Achievement build();

    AchievementBuilder id(int id);

    AchievementBuilder name(String name);

    AchievementBuilder minValue(int minValue);

    AchievementBuilder maxValue(int maxValue);

    AchievementBuilder description(String description);

    AchievementBuilder iconUrl(String iconUrl);

    AchievementBuilder levels(List<AchievementLevel> levels);

}

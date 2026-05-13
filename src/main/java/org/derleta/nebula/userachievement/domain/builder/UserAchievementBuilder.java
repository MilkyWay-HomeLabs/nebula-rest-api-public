package org.derleta.nebula.userachievement.domain.builder;

import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.userachievement.domain.model.Achievement;

public interface UserAchievementBuilder {

    UserAchievement build();

    UserAchievementBuilder userId(long userId);

    UserAchievementBuilder achievementId(int achievementId);

    UserAchievementBuilder value(int value);

    UserAchievementBuilder level(int level);

    UserAchievementBuilder progress(int progress);

    UserAchievementBuilder achievement(Achievement achievement);

}

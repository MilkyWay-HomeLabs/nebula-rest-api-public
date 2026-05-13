package org.derleta.nebula.userachievement.application.port.in;

import org.derleta.nebula.userachievement.domain.model.UserAchievement;

/** Input port — fetches a single user achievement by composite key. */
public interface GetUserAchievementUseCase {

    UserAchievement getUserAchievement(long userId, int achievementId);
}

